package ua.qalight.telegramqalightbot.enums;

/**
 * You can find more information about emoji in this link:
 * https://github.com/vdurmont/emoji-java/blob/master/EMOJIS.md
 *
 * */
public enum Emoji {
    JOY(":joy:"),
    SMILE(":smile:"),
    WINK(":wink:"),
    BLUSH(":blush:"),
    WINKING_EYE(":stuck_out_tongue_winking_eye:"),
    OK(":ok_hand:"),
    DOLLAR(":dollar:"),
    EURO(":euro:");

    private String emoji;

    Emoji(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }
}
