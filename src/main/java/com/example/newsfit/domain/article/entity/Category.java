package com.example.newsfit.domain.article.entity;

public enum Category {
    정치("정치"),
    경제("경제"),
    사회("사회"),
    생활_문화("생활/문화"),
    세계("세계"),
    기술_IT("기술/IT"),
    연예("연예"),
    스포츠("스포츠");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public static Category fromDisplayName(String displayName) {
        for (Category category : Category.values()) {
            if (category.displayName.equals(displayName)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + displayName);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
