package com.dino.learn.service;

import com.dino.learn.entity.poem.Poem;
import com.dino.learn.entity.vocab.VocabItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LearningDataService {

    private final EntityManager entityManager;

    public LearningDataService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Poem> getPoems(String category) {
        if (category != null && !category.isEmpty()) {
            TypedQuery<Poem> query = entityManager.createQuery(
                    "SELECT p FROM Poem p WHERE p.category = :category ORDER BY p.orderNum",
                    Poem.class);
            query.setParameter("category", category);
            return query.getResultList();
        }
        return entityManager.createQuery(
                "SELECT p FROM Poem p ORDER BY p.orderNum", Poem.class)
                .getResultList();
    }

    public Optional<Poem> getPoem(Long id) {
        return Optional.ofNullable(entityManager.find(Poem.class, id));
    }

    public List<Map<String, Object>> getAlphabet() {
        List<Map<String, Object>> alphabet = new ArrayList<>();
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        String[] examples = {"Apple", "Ball", "Cat", "Dog", "Egg", "Fish", "Girl", "Hat", "Ice",
                "Juice", "Kite", "Lion", "Moon", "Nest", "Orange", "Pig", "Queen",
                "Rabbit", "Sun", "Tree", "Umbrella", "Violin", "Water", "X-ray",
                "Yarn", "Zebra"};

        for (int i = 0; i < letters.length; i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("letter", letters[i]);
            item.put("example", examples[i]);
            item.put("order", i + 1);
            alphabet.add(item);
        }
        return alphabet;
    }

    public List<Map<String, Object>> getColors() {
        List<Map<String, Object>> colors = new ArrayList<>();
        addColor(colors, "red", "#FF0000", "红色");
        addColor(colors, "blue", "#0000FF", "蓝色");
        addColor(colors, "green", "#00FF00", "绿色");
        addColor(colors, "yellow", "#FFFF00", "黄色");
        addColor(colors, "orange", "#FFA500", "橙色");
        addColor(colors, "purple", "#800080", "紫色");
        addColor(colors, "pink", "#FFC0CB", "粉色");
        addColor(colors, "black", "#000000", "黑色");
        addColor(colors, "white", "#FFFFFF", "白色");
        addColor(colors, "brown", "#A52A2A", "棕色");
        addColor(colors, "gray", "#808080", "灰色");
        addColor(colors, "gold", "#FFD700", "金色");
        return colors;
    }

    private void addColor(List<Map<String, Object>> list, String name, String hex, String chinese) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("hex", hex);
        item.put("chinese", chinese);
        list.add(item);
    }

    public List<Map<String, Object>> getShapes() {
        List<Map<String, Object>> shapes = new ArrayList<>();
        addShape(shapes, "circle", "圆形", "round");
        addShape(shapes, "square", "正方形", "4 equal sides");
        addShape(shapes, "triangle", "三角形", "3 sides");
        addShape(shapes, "rectangle", "长方形", "2 long, 2 short sides");
        addShape(shapes, "oval", "椭圆形", "like an egg");
        addShape(shapes, "star", "星形", "5 points");
        addShape(shapes, "heart", "心形", "love shape");
        addShape(shapes, "diamond", "菱形", "kite shape");
        addShape(shapes, "pentagon", "五边形", "5 sides");
        addShape(shapes, "hexagon", "六边形", "6 sides");
        return shapes;
    }

    private void addShape(List<Map<String, Object>> list, String name, String chinese, String description) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("chinese", chinese);
        item.put("description", description);
        list.add(item);
    }

    public List<Map<String, Object>> getComparePairs() {
        List<Map<String, Object>> pairs = new ArrayList<>();
        addComparePair(pairs, "big", "small", "大", "小");
        addComparePair(pairs, "tall", "short", "高", "矮");
        addComparePair(pairs, "hot", "cold", "热", "冷");
        addComparePair(pairs, "fast", "slow", "快", "慢");
        addComparePair(pairs, "long", "short", "长", "短");
        addComparePair(pairs, "heavy", "light", "重", "轻");
        addComparePair(pairs, "loud", "quiet", "大声", "安静");
        addComparePair(pairs, "happy", "sad", "开心", "伤心");
        addComparePair(pairs, "clean", "dirty", "干净", "脏");
        addComparePair(pairs, "full", "empty", "满", "空");
        return pairs;
    }

    private void addComparePair(List<Map<String, Object>> list, String word1, String word2,
                                String chinese1, String chinese2) {
        Map<String, Object> item = new LinkedHashMap<>();
        Map<String, String> first = new LinkedHashMap<>();
        first.put("word", word1);
        first.put("chinese", chinese1);
        Map<String, String> second = new LinkedHashMap<>();
        second.put("word", word2);
        second.put("chinese", chinese2);
        item.put("first", first);
        item.put("second", second);
        list.add(item);
    }

    public List<VocabItem> getVocabItems(String categoryKey) {
        TypedQuery<VocabItem> query = entityManager.createQuery(
                "SELECT v FROM VocabItem v WHERE v.categoryKey = :categoryKey ORDER BY v.orderNum",
                VocabItem.class);
        query.setParameter("categoryKey", categoryKey);
        return query.getResultList();
    }
}
