import os
import base64
import requests
import json
import logging
from flask import Flask, request, Response, jsonify

app = Flask(__name__)
app.config['SECRET_KEY'] = os.environ.get("GEMINI_API_KEY")

app.json.ensure_ascii = False

if __name__ != '__main__':
    gunicorn_logger = logging.getLogger('gunicorn.error')
    app.logger.handlers = gunicorn_logger.handlers
    app.logger.setLevel(gunicorn_logger.level)

GEMINI_IMAGE_MODEL = "gemini-2.0-flash-preview-image-generation"
GEMINI_TEXT_MODEL = "gemini-2.5-flash"
BASE_GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models"

def generate_image_from_prompt(prompt):
    try:
        api_url = f"{BASE_GEMINI_URL}/{GEMINI_IMAGE_MODEL}:generateContent?key={app.config['SECRET_KEY']}"
        payload = {
            "contents": [{"parts": [{"text": prompt}]}],
            "generationConfig": {"responseModalities": ["IMAGE", "TEXT"]}
        }
        response = requests.post(api_url, headers={"Content-Type": "application/json"}, data=json.dumps(payload))
        response.raise_for_status()
        result = response.json()

        if "candidates" in result and result["candidates"] and "content" in result["candidates"][0]:
            parts = result["candidates"][0]["content"].get("parts", [])
            for part in parts:
                if "inlineData" in part and "data" in part["inlineData"]:
                    b64_data = part["inlineData"]["data"]
                    binary_data = base64.b64decode(b64_data)
                    return Response(binary_data, mimetype="image/png")
        
        return jsonify({"error": "API 응답에서 이미지 데이터를 찾을 수 없습니다."}), 500

    except requests.exceptions.HTTPError as e:
        return jsonify({"error": f"API 호출 실패 (HTTP 상태 코드: {e.response.status_code})", "details": e.response.text}), e.response.status_code
    except Exception as e:
        return jsonify({"error": f"이미지 생성 중 예상치 못한 오류 발생: {e}"}), 500

def generate_text_from_prompt(prompt):
    try:
        api_url = f"{BASE_GEMINI_URL}/{GEMINI_TEXT_MODEL}:generateContent?key={app.config['SECRET_KEY']}"
        payload = {
            "contents": [{"parts": [{"text": prompt}]}],
            "generationConfig": {"responseModalities": ["TEXT"]}
        }
        response = requests.post(api_url, headers={"Content-Type": "application/json"}, data=json.dumps(payload))
        response.raise_for_status()
        result = response.json()

        if "candidates" in result and result["candidates"] and "content" in result["candidates"][0]:
            parts = result["candidates"][0]["content"].get("parts", [])
            for part in parts:
                if "text" in part:
                    # Gemini가 생성한 텍스트에서 JSON 부분만 추출
                    json_text = part["text"].strip().replace("```json", "").replace("```", "")
                    try:
                        return jsonify(json.loads(json_text))
                    except json.JSONDecodeError:
                        return jsonify({"error": "API 응답이 유효한 JSON 형식이 아닙니다.", "raw_response": part["text"]}), 500
                        
        return jsonify({"error": "API 응답에서 텍스트 데이터를 찾을 수 없습니다."}), 500

    except requests.exceptions.HTTPError as e:
        return jsonify({"error": f"API 호출 실패 (HTTP 상태 코드: {e.response.status_code})", "details": e.response.text}), e.response.status_code
    except Exception as e:
        return jsonify({"error": f"텍스트 생성 중 예상치 못한 오류 발생: {e}"}), 500

@app.route("/image/product", methods=["GET"])
def generate_product_image():
    name = request.args.get("name")
    if not name:
        return jsonify({"error": "name 파라미터가 필요합니다."}), 400

    prompt = f"성남시에 있는 시장에서 파는 '{name}' 상품의 사실적인 사진, 흰색 배경 (photorealistic, white background)"
    return generate_image_from_prompt(prompt)

@app.route("/image/shop", methods=["GET"])
def generate_shop_image():
    title = request.args.get("title")
    market_name = request.args.get("market_name")
    description = request.args.get("description")
    if not title or not description:
        return jsonify({"error": "title과 description 파라미터가 모두 필요합니다."}), 400
    
    prompt = f"{market_name}에 있는 '{title}'이라는 가게. 가게 특징은 '{description}'. 이 가게의 전면을 보여주는 사실적인 사진, 가게 이름만 간판에 포함 (photorealistic, front view of the store, including the store name sign, not including the feature to text)"
    return generate_image_from_prompt(prompt)

@app.route("/text/description", methods=["GET"])
def generate_store_description():
    title = request.args.get("title")
    if not title:
        return jsonify({"error": "title 파라미터가 필요합니다."}), 400
    
    prompt = f"'{title}' 이라는 상점명을 가진 가게가 있습니다. 이 가게의 'category'와 'description'을 JSON 객체 형식으로 생성해 주세요. category는 한 단어의 명사, description은 50자 이내의 한 문장으로, 모두 한국어로 작성해주세요. 예시: {{ \"category\": \"반찬가게\", \"description\": \"매일 아침 신선한 재료로 만드는 정성 가득한 수제 반찬 전문점입니다.\" }}"
    return generate_text_from_prompt(prompt)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)