package com.qingyu.controller;

import com.qingyu.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private static final Map<String, List<String>> IMAGE_LIBRARY = new HashMap<>();

    static {
        IMAGE_LIBRARY.put("校园", Arrays.asList(
            "/api/placeholder/800x600?seed=1&text=校园",
            "/api/placeholder/800x600?seed=20&text=图书馆",
            "/api/placeholder/800x600?seed=47&text=毕业",
            "/api/placeholder/800x600?seed=48&text=青春",
            "/api/placeholder/800x600?seed=50&text=教室",
            "/api/placeholder/800x600?seed=51&text=操场"
        ));
        IMAGE_LIBRARY.put("美食", Arrays.asList(
            "/api/placeholder/800x600?seed=5&text=美食",
            "/api/placeholder/800x600?seed=23&text=拉面",
            "/api/placeholder/800x600?seed=24&text=料理",
            "/api/placeholder/800x600?seed=36&text=泡面",
            "/api/placeholder/800x600?seed=39&text=海鲜",
            "/api/placeholder/800x600?seed=52&text=甜点"
        ));
        IMAGE_LIBRARY.put("旅行", Arrays.asList(
            "/api/placeholder/800x600?seed=6&text=古镇",
            "/api/placeholder/800x600?seed=7&text=旅行",
            "/api/placeholder/800x600?seed=25&text=海边",
            "/api/placeholder/800x600?seed=40&text=大理",
            "/api/placeholder/800x600?seed=41&text=丽江",
            "/api/placeholder/800x600?seed=42&text=香格里拉"
        ));
        IMAGE_LIBRARY.put("编程", Arrays.asList(
            "/api/placeholder/800x600?seed=4&text=代码",
            "/api/placeholder/800x600?seed=22&text=编程",
            "/api/placeholder/800x600?seed=38&text=VSCode",
            "/api/placeholder/800x600?seed=53&text=开发",
            "/api/placeholder/800x600?seed=54&text=调试",
            "/api/placeholder/800x600?seed=55&text=终端"
        ));
        IMAGE_LIBRARY.put("音乐", Arrays.asList(
            "/api/placeholder/800x600?seed=8&text=音乐",
            "/api/placeholder/800x600?seed=27&text=专辑",
            "/api/placeholder/800x600?seed=45&text=街头音乐",
            "/api/placeholder/800x600?seed=56&text=耳机",
            "/api/placeholder/800x600?seed=57&text=吉他",
            "/api/placeholder/800x600?seed=58&text=钢琴"
        ));
        IMAGE_LIBRARY.put("读书", Arrays.asList(
            "/api/placeholder/800x600?seed=9&text=读书",
            "/api/placeholder/800x600?seed=28&text=人类简史",
            "/api/placeholder/800x600?seed=35&text=茶与书",
            "/api/placeholder/800x600?seed=46&text=小王子",
            "/api/placeholder/800x600?seed=59&text=书架",
            "/api/placeholder/800x600?seed=60&text=笔记"
        ));
        IMAGE_LIBRARY.put("健身", Arrays.asList(
            "/api/placeholder/800x600?seed=13&text=跑步",
            "/api/placeholder/800x600?seed=31&text=健身",
            "/api/placeholder/800x600?seed=43&text=举铁",
            "/api/placeholder/800x600?seed=61&text=瑜伽",
            "/api/placeholder/800x600?seed=62&text=游泳",
            "/api/placeholder/800x600?seed=63&text=拉伸"
        ));
        IMAGE_LIBRARY.put("宠物", Arrays.asList(
            "/api/placeholder/800x600?seed=14&text=橘猫",
            "/api/placeholder/800x600?seed=15&text=猫咪",
            "/api/placeholder/800x600?seed=32&text=猫咪",
            "/api/placeholder/800x600?seed=33&text=萌宠",
            "/api/placeholder/800x600?seed=44&text=猫咪",
            "/api/placeholder/800x600?seed=64&text=小狗"
        ));
        IMAGE_LIBRARY.put("夜晚", Arrays.asList(
            "/api/placeholder/800x600?seed=19&text=深夜",
            "/api/placeholder/800x600?seed=21&text=星空",
            "/api/placeholder/800x600?seed=29&text=雨夜",
            "/api/placeholder/800x600?seed=30&text=霓虹",
            "/api/placeholder/800x600?seed=65&text=月光",
            "/api/placeholder/800x600?seed=66&text=灯火"
        ));
        IMAGE_LIBRARY.put("风景", Arrays.asList(
            "/api/placeholder/800x600?seed=11&text=晚霞",
            "/api/placeholder/800x600?seed=12&text=日落",
            "/api/placeholder/800x600?seed=37&text=湖边",
            "/api/placeholder/800x600?seed=67&text=山川",
            "/api/placeholder/800x600?seed=68&text=花海",
            "/api/placeholder/800x600?seed=69&text=田野"
        ));
        IMAGE_LIBRARY.put("艺术", Arrays.asList(
            "/api/placeholder/800x600?seed=17&text=水彩",
            "/api/placeholder/800x600?seed=34&text=美术馆",
            "/api/placeholder/800x600?seed=70&text=油画",
            "/api/placeholder/800x600?seed=71&text=素描",
            "/api/placeholder/800x600?seed=72&text=雕塑",
            "/api/placeholder/800x600?seed=73&text=展览"
        ));
    }

    private static final List<String> KEYWORD_MAPPING = Arrays.asList(
        "校园,学校,教学,图书馆,食堂,毕业,考试,上课",
        "美食,吃,食堂,拉面,螺蛳粉,火锅,烧烤,菜,做饭,泡面",
        "旅行,古镇,海边,山,岛,风景,散步,城市",
        "编程,代码,Bug,程序员,VS Code,JavaScript,Java,开发",
        "音乐,歌,耳机,专辑,唱,吉他",
        "读书,书,读,小说,诗,文学",
        "健身,跑步,运动,锻炼,训练,举铁,引体向上",
        "宠物,猫,狗,橘猫,喵,汪",
        "夜晚,深夜,凌晨,晚安,星星,月亮,便利店",
        "风景,日落,晚霞,天空,湖,雨,花,樱花,春天",
        "艺术,画,水彩,美术馆,莫奈,颜料"
    );

    @GetMapping("/search")
    public Result<List<String>> search(@RequestParam String keyword,
                                       @RequestParam(defaultValue = "3") int count) {
        String matchedCategory = matchCategory(keyword);
        List<String> images = IMAGE_LIBRARY.getOrDefault(matchedCategory,
            IMAGE_LIBRARY.get("风景"));

        List<String> result = new ArrayList<>(images);
        Collections.shuffle(result);
        return Result.success(result.subList(0, Math.min(count, result.size())));
    }

    private String matchCategory(String keyword) {
        String[] categories = {"校园", "美食", "旅行", "编程", "音乐", "读书", "健身", "宠物", "夜晚", "风景", "艺术"};

        for (int i = 0; i < KEYWORD_MAPPING.size(); i++) {
            String[] keys = KEYWORD_MAPPING.get(i).split(",");
            for (String key : keys) {
                if (keyword.contains(key.trim())) {
                    return categories[i];
                }
            }
        }
        return "风景";
    }
}
