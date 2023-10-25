package ua.qalight.telegramqalightbot.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.qalight.telegramqalightbot.config.BotConfig;
import ua.qalight.telegramqalightbot.enums.Emoji;
import ua.qalight.telegramqalightbot.utils.KeyboardRow;

@Component
//@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;

    @Autowired
    public TelegramBot(BotConfig config) {
        super(config.getBotToken());
        this.config = config;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (message) {
                case "/start":
                    startCommand(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/info":
                    sendMessage(chatId, "This is test telegram bot from QAlight");
                    break;
                case "/currencyJSON":
                    handleRequest(chatId, "Select currency: ", "JSON");
                    break;
                case "/currencyXML":
                    handleRequest(chatId, "Select currency: ", "XML");
                    break;
                default:
                    sendMessage(chatId, "Sorry, command wos not recognized.");
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            CurrencyService currencyService = callbackData.endsWith("JSON") ?
                    new CurrencyJSONService() : new CurrencyXMLService();
            String currency=callbackData.substring(0,3);

            String message = currencyService.getResponse(currency);
            sendMessage(chatId, message);
        }
    }

    private void handleRequest(long chatId, String selectCurrency, String format) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(selectCurrency);

        InlineKeyboardMarkup markup = KeyboardRow.createKeyboardForCurrency(format);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleJSON(long chatId, String selectCurrency) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(selectCurrency);

        InlineKeyboardMarkup markup = KeyboardRow.createKeyboardForCurrency("d");
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private void startCommand(long chatId, String firstName) {
        String response = EmojiParser.parseToUnicode("Hi, " + firstName + ", nice to meet you!" + Emoji.BLUSH.getEmoji() + Emoji.JOY.getEmoji() + Emoji.WINK.getEmoji());
        sendMessage(chatId, response);
    }

    private void sendMessage(long chatId, String testToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(testToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
}
