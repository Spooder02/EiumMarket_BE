# app.py
import os
import base64
import requests
import json
from flask import Flask, request, Response, jsonify, render_template_string

# Flask 앱 인스턴스 생성
app = Flask(__name__)

# HTML 템플릿: 이미지 생성 폼과 설명을 포함합니다.
INDEX_HTML = """
<!doctype html>
<html lang="ko">
<head>
  <meta charset="utf-8">
  <title>Gemini 이미지 생성</title>
  <style>
    body { font-family: sans-serif; text-align: center; margin-top: 50px; }
    form { margin-bottom: 20px; }
    textarea { width: 80%; max-width: 600px; padding: 10px; border-radius: 8px; border: 1px solid #ccc; }
    button { padding: 10px 20px; font-size: 16px; border: none; border-radius: 8px; background-color: #4285F4; color: white; cursor: pointer; }
    button:hover { background-color: #357ae8; }
    .image-container { margin-top: 20px; }
    .image-container img { max-width: 100%; height: auto; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
    .error-message { color: red; margin-top: 20px; }
    #download-btn { margin-top: 10px; display: none; } /* 처음에는 숨겨져 있음 */
  </style>
</head>
<body>
  <h1>Gemini 이미지 생성 (1024x1024)</h1>
  <form id="image-form">
    <textarea name="prompt" rows="4" cols="60" placeholder="프롬프트를 입력하세요 (예: 푸른 하늘 아래 활짝 핀 해바라기 밭)"></textarea><br/><br/>
    <button type="submit">생성</button>
  </form>
  <div class="image-container">
    <img id="generated-image" alt="생성된 이미지">
  </div>
  <a id="download-btn" href="#">이미지 다운로드</a>
  <div class="error-message" id="message"></div>

  <script>
    document.getElementById('image-form').addEventListener('submit', async function(e) {
      e.preventDefault();
      const prompt = document.querySelector('textarea[name="prompt"]').value;
      const messageElement = document.getElementById('message');
      const imageElement = document.getElementById('generated-image');
      const downloadButton = document.getElementById('download-btn');

      if (!prompt.trim()) {
        messageElement.textContent = "프롬프트가 비어 있습니다.";
        imageElement.src = '';
        downloadButton.style.display = 'none';
        return;
      }

      messageElement.textContent = "이미지를 생성하는 중입니다. 잠시만 기다려 주세요...";
      imageElement.src = '';
      downloadButton.style.display = 'none';

      try {
        const response = await fetch('/generate-image', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ prompt: prompt })
        });

        if (response.ok) {
          const blob = await response.blob();
          const imageUrl = URL.createObjectURL(blob);
          imageElement.src = imageUrl;
          downloadButton.href = imageUrl; // 다운로드 버튼에 이미지 URL 설정
          downloadButton.download = "generated_image.png"; // 다운로드 파일명 설정
          downloadButton.style.display = 'block'; // 버튼 표시
          messageElement.textContent = "이미지 생성이 완료되었습니다.";
        } else {
          const errorData = await response.json();
          messageElement.textContent = "오류 발생: " + (errorData.error || "알 수 없는 오류");
        }
      } catch (error) {
        messageElement.textContent = "네트워크 오류가 발생했습니다: " + error.message;
      }
    });
  </script>
</body>
</html>
"""

# Flask 앱 인스턴스 생성
app = Flask(__name__)

# Gemini API 키를 환경 변수에서 로드합니다.
app.config['SECRET_KEY'] = os.environ.get("GEMINI_API_KEY")

@app.route("/", methods=["GET"])
def index():
    return render_template_string(INDEX_HTML)

@app.route("/generate-image", methods=["POST"])
def generate_image():
    try:
        data = request.get_json(silent=True) or {}
        prompt = data.get("prompt", "").strip()
        if not prompt:
            return jsonify({"error": "prompt가 비어 있습니다."}), 400

        # gemini-2.0-flash-preview-image-generation 모델의 API 엔드포인트
        api_url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-preview-image-generation:generateContent?key={app.config['SECRET_KEY']}"

        # 요청 페이로드
        payload = {
            "contents": [{
                "parts": [{"text": prompt}]
            }],
            "generationConfig": {
                "responseModalities": ["IMAGE", "TEXT"]
            }
        }

        # API 호출
        response = requests.post(
            api_url,
            headers={"Content-Type": "application/json"},
            data=json.dumps(payload)
        )

        # 응답 상태 코드 확인
        response.raise_for_status()
        
        # JSON 응답에서 이미지 데이터 추출
        result = response.json()
        
        # 응답 구조를 수정하여 'content' 필드에 접근합니다.
        if "candidates" in result and result["candidates"] and "content" in result["candidates"][0]:
            parts = result["candidates"][0]["content"].get("parts", [])
            for part in parts:
                if "inlineData" in part and "data" in part["inlineData"]:
                    b64_data = part["inlineData"]["data"]
                    binary_data = base64.b64decode(b64_data)
                    return Response(binary_data, mimetype="image/png")

        # 모든 후보를 확인했음에도 이미지 데이터를 찾지 못한 경우
        return jsonify({"error": "API 응답에서 이미지 데이터를 찾을 수 없습니다."}), 500

    except requests.exceptions.RequestException as e:
        return jsonify({"error": f"API 요청 실패: {e}"}), 500
    except Exception as e:
        return jsonify({"error": f"예상치 못한 오류 발생: {e}"}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
