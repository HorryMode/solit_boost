package slcd.boost.boost.General;

public final class Constants {
    //Константы авторизации
    public static final String BAD_CREDENTIALS_MESSAGE = "Передано неверное имя пользователя или пароль";

    //Константы файлов
    public static final int MAX_FILE_SIZE = 10 * 1024 * 1024; //10 МБ
    public static final String FILE_SIZE_EXCEEDS_MAX_SIZE_MESSAGE = "Файл должен иметь размер не более 10 МБ";

    //Константы для РВ
    public static final String RM_PROTOCOL_NOT_FOUND_MESSAGE = "Протокол о регулярной встрече с id %s не найден";
    public static final String RM_ATTACHMENT_NOT_FOUND_MESSAGE = "Протокол о регулярной встрече с id %s не найден";
    public static final String SAVE_FILE_DIRECTORY = "/conversationAttachments";

    public static final String ONLY_TEAM_LEADER_HAVE_ACCESS_MESSAGE = "Только руководитель команды может выполнять данный запрос";

    public static final String RM_PROTOCOL_NAME = "Встреча %s";

    //Константы для пользователей
    public static final String USER_NOT_FOUND_MESSAGE = "Пользователь с id %d не найден";
    public static final String PART_TIME_STRING = "Частичная занятость(4ч)";
    public static final String FULL_TIME_STRING = "Полная занятость(8ч)";

    public static final String USER_CANT_BE_TL_MESSAGE = "Нельзя назначить пользователя тимлидером своего тимлидера";

    public static final String USER_ALREADY_TL_MESSAGE = "Пользователь с id %d уже является лидером пользователя с id %d";
}
