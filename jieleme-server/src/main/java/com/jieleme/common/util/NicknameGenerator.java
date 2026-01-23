package com.jieleme.common.util;

import java.util.Random;

/**
 * 随机昵称生成器
 * 格式: 形容词 + 的 + 动物 + 4位随机数
 * 例如: 温柔的海豚4821
 */
public class NicknameGenerator {
    
    private static final String[] ADJECTIVES = {
        "温柔", "安静", "勇敢", "快乐", "沉稳", "清醒", "坚定", "自由", "专注",
        "热情", "冷静", "活泼", "优雅", "善良", "智慧", "坚强", "温暖", "乐观"
    };
    
    private static final String[] ANIMALS = {
        "海豚", "狐狸", "鲸鱼", "猫头鹰", "小鹿", "北极熊", "松鼠", "企鹅",
        "海豹", "熊猫", "考拉", "兔子", "小猫", "小狗", "天鹅", "海鸥"
    };
    
    private static final Random random = new Random();
    
    /**
     * 生成随机昵称
     */
    public static String generate() {
        String adjective = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String animal = ANIMALS[random.nextInt(ANIMALS.length)];
        int number = random.nextInt(10000); // 0-9999
        
        return String.format("%s的%s%04d", adjective, animal, number);
    }
    
    public static void main(String[] args) {
        // 测试生成几个昵称
        for (int i = 0; i < 10; i++) {
            System.out.println(generate());
        }
    }
}
