package com.eiummarket.demo.Utils;

import com.eiummarket.demo.dto.MarketDto;
import com.eiummarket.demo.model.Market;
import com.eiummarket.demo.model.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SearchUtils {
    private SearchUtils() {}

    public static String sanitize(String keyword) {
        if (keyword == null) return null;
        String trimmed = keyword.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static String escapeLike(String s) {
        // JPA LIKE에서 %, _ 이스케이프 필요시 사용 (여기선 단순 소문자/트림만)
        return s.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
    public static void addAllShops(Map<Long, Shop> target, List<Shop> source) {
        if (source == null) return;
        for (Shop s : source) if (s != null) target.putIfAbsent(s.getShopId(), s);
    }

    public static void addAllMarkets(Map<Long, Market> target, List<Market> source) {
        if (source == null) return;
        for (Market m : source) if (m != null) target.putIfAbsent(m.getMarketId(), m);
    }

    public static void sortByPageableShop(List<Shop> list, Pageable pageable) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            list.sort(Comparator.comparing(Shop::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                    .thenComparing(Comparator.comparing(Shop::getShopId, Comparator.nullsLast(Long::compareTo))));
            return;
        }
        Comparator<Shop> comp = null;
        for (Sort.Order order : pageable.getSort()) {
            Comparator<Shop> c = switch (order.getProperty()) {
                case "favoriteCount" -> Comparator.comparingLong(s -> s.getFavoriteCount() == null ? 0L : s.getFavoriteCount());
                case "name" -> Comparator.comparing(Shop::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                case "createdAt" -> Comparator.comparing(Shop::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
                default -> null;
            };
            if (c != null) {
                if (order.isDescending()) c = c.reversed();
                comp = (comp == null) ? c : comp.thenComparing(c);
            }
        }
        if (comp == null) {
            comp = Comparator.comparing(Shop::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }
        comp = comp.thenComparing(Comparator.comparing(Shop::getShopId, Comparator.nullsLast(Long::compareTo)));
        list.sort(comp);
    }
    public static void sortByPageableMarket(List<Market> list, Pageable pageable) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            Comparator<Market> defaultComp =
                    Comparator.comparing(Market::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                            .thenComparing(Comparator.comparing(
                                    Market::getMarketId,
                                    Comparator.nullsLast(Long::compareTo)
                            ));
            list.sort(defaultComp);
            return;
        }

        Comparator<Market> comp = null;

        for (Sort.Order order : pageable.getSort()) {
            Comparator<Market> c = switch (order.getProperty()) {
                case "name" -> Comparator.comparing(
                        Market::getName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                );
                case "createdAt" -> Comparator.comparing(
                        Market::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                );
                default -> null;
            };

            if (c != null) {
                if (order.isDescending()) c = c.reversed();
                comp = (comp == null) ? c : comp.thenComparing(c);
            }
        }

        if (comp == null) {
            comp = Comparator.comparing(Market::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        }
        comp = comp.thenComparing(Comparator.comparing(
                Market::getMarketId,
                Comparator.nullsLast(Long::compareTo)
        ));

        list.sort(comp);
    }
}

