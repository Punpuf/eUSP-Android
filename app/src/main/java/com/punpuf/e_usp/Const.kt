package com.punpuf.e_usp

class Const {
    companion object {
        /*
            ******************************
            ********** DATABASE **********
            ******************************
         */
        const val ERROR_NETWORK = 20
        const val ERROR_INTERNAL = 21
        const val ERROR_UNKNOWN = 22
        const val ERROR_SERVER_DENIED = 23

        const val DB_CARD_NAME = "database"
        const val DB_CARD_VERSION = 1

        // Table for the long lasting Tokens
        const val TABLE_TOKEN_NAME = "table_token"
        const val TABLE_TOKEN_FIELD_TOKEN = "field_token"

        const val TABLE_TOKEN_VALUE_ID_ACCESS_TOKEN_TOKEN = 100
        const val TABLE_TOKEN_VALUE_ID_ACCESS_TOKEN_TOKEN_SECRET = 101
        const val TABLE_TOKEN_VALUE_ID_WS_USER_ID = 102

        // Table for the user profiles
        const val TABLE_USER_PROFILE_NAME = "table_user_profile"
        const val TABLE_USER_PROFILE_FIELD_USER_TYPE = "field_user_type"
        const val TABLE_USER_PROFILE_FIELD_USER_TYPE_CODE = "field_user_type_code"
        const val TABLE_USER_PROFILE_FIELD_USER_GROUP = "field_user_group"
        const val TABLE_USER_PROFILE_FIELD_QR_CODE_TOKEN = "field_qr_code_token"
        const val TABLE_USER_PROFILE_FIELD_QR_CODE_TOKEN_EXPIRATION_DATE = "field_qr_code_token_expiration"
        const val TABLE_USER_PROFILE_FIELD_USER_DEPARTMENT = "field_department"
        const val TABLE_USER_PROFILE_FIELD_CARD_EXPIRATION_DATE = "field_card_expiration_date"

        // Table for user info
        const val TABLE_USER_INFO_NAME = "table_user_info"
        const val TABLE_USER_INFO_FIELD_NUMBER_USP = "field_number_usp"
        const val TABLE_USER_INFO_FIELD_NAME = "field_name"
        const val TABLE_USER_INFO_FIELD_DEPARTMENT_NAME = "field_department"

        // Table for profile pic location
        const val TABLE_PROFILE_PIC_NAME = "table_profile_pic"
        const val TABLE_PROFILE_PIC_FIELD_LOCATION = "field_pic_location"
        const val TABLE_PROFILE_PIC_FIELD_DOWNLOAD_DATE = "field_pic_download_date"

        //Table for boleto
        const val TABLE_BOLETO = "table_boleto"
        const val TABLE_BOLETO_ID = "table_boleto_id"
        const val TABLE_BOLETO_AMOUNT = "table_boleto_amount"
        const val TABLE_BOLETO_BARCODE = "table_boleto_barcode"
        const val TABLE_BOLETO_EXPIRATION_DATE = "table_boleto_expiration_date"

        //Table for restaurants
        const val TABLE_RESTAURANT = "table_restaurant"
        const val TABLE_RESTAURANT_FIELD_NAME = "table_restaurant_name"
        const val TABLE_RESTAURANT_FIELD_CAMPUS_NAME = "table_restaurant_campus_name"
        const val TABLE_RESTAURANT_FIELD_THUMBNAIL_URL = "table_restaurant_thumbnail_url"
        const val TABLE_RESTAURANT_FIELD_ADDRESS = "table_restaurant_address"
        const val TABLE_RESTAURANT_FIELD_LATITUDE = "table_restaurant_latitude"
        const val TABLE_RESTAURANT_FIELD_LONGITUDE = "table_restaurant_longitude"
        const val TABLE_RESTAURANT_FIELD_PHONE_NUMBER = "table_restaurant_phone_number"
        const val TABLE_RESTAURANT_FIELD_WORKING_HOURS_WEEKDAYS = "table_restaurant_working_hours_weekdays"
        const val TABLE_RESTAURANT_FIELD_WORKING_HOURS_SATURDAY = "table_restaurant_working_hours_saturday"
        const val TABLE_RESTAURANT_FIELD_WORKING_HOURS_SUNDAY = "table_restaurant_working_hours_sunday"
        const val TABLE_RESTAURANT_FIELD_CASHIER_INFO = "table_restaurant_cashier_info"
        const val TABLE_RESTAURANT_FIELD_LAST_USED = "table_restaurant_last_used"

        //Table for restaurant menu
        const val TABLE_MENU = "table_menu"
        const val TABLE_MENU_FIELD_RESTAURANT_ID = "table_menu_restaurant_id"
        const val TABLE_MENU_FIELD_OBSERVATION = "table_menu_observation"
        const val TABLE_MENU_FIELD_MEALS = "table_menu_meals"
        const val TABLE_MENU_FIELD_EXPIRATION_DATE = "table_menu_expiration_date"

        //Table for selected restaurant
        const val TABLE_SELECTED_RESTAURANT = "table_selected"
        const val TABLE_SELECTED_RESTAURANT_ID = "table_selected_id"
        const val TABLE_SELECTED_RESTAURANT_SELECTION_DATE = "table_selected_selection_date"

        // Table for Book User
        const val TABLE_BOOK_USER = "table_book_user"
        const val TABLE_BOOK_USER_SYS_NO = "table_book_user_sys_no"
        const val TABLE_BOOK_USER_TYPE = "table_book_user_type"
        const val TABLE_BOOK_USER_ITEM_SEQ = "table_book_user_item_seq"
        const val TABLE_BOOK_USER_HOLD_SEQ = "table_book_user_hold_seq"
        const val TABLE_BOOK_USER_CALL_NO = "table_book_user_call_no"
        const val TABLE_BOOK_USER_TITLE = "table_book_user_title"
        const val TABLE_BOOK_USER_AUTHORS = "table_book_user_authors"
        const val TABLE_BOOK_USER_LIBRARY_CODE = "table_book_user_library_code"
        const val TABLE_BOOK_USER_LIBRARY_NAME = "table_book_user_library_name"
        const val TABLE_BOOK_USER_MEDIA_TYPE = "table_book_user_media_type"
        const val TABLE_BOOK_USER_DUE_DATE = "table_book_user_due_date"
        const val TABLE_BOOK_USER_RETURN_DATE = "table_book_user_return_date"
        const val TABLE_BOOK_USER_NO_RENEW = "table_book_user_no_renew"

        //URL Endpoints for Network Services
        const val NETWORK_BASE_URL = "https://uspdigital.usp.br/"
        const val NETWORK_ENDPOINT_LIST_PROFILES = "mobile/servicos/ecard/listarPerfis"
        const val NETWORK_ENDPOINT_CARD_PICTURE = "mobile/servicos/ecard/fotoCartao"
        const val NETWORK_ENDPOINT_RENEW_QRCODE = "mobile/servicos/ecard/renovarQr"
        const val NETWORK_ENDPOINT_REGISTER = "mobile/servicos/oauth/registrar"
        const val NETWORK_ENDPOINT_LOGOUT = "mobile/servicos/oauth/sair"

        const val NETWORK_ENDPOINT_FETCH_ACCOUNT_BALANCE = "mobile/servicos/cardapio/consultarSaldo"
        const val NETWORK_ENDPOINT_GENERATE_BOLETO = "mobile/servicos/cardapio/gerarBoleto"
        const val NETWORK_ENDPOINT_FETCH_ONGOING_BOLETO = "mobile/servicos/cardapio/boletosEmAberto"
        const val NETWORK_ENDPOINT_CANCEL_BOLETO = "mobile/servicos/cardapio/cancelarBoleto"

        const val NETWORK_ENDPOINT_FETCH_RESTAURANT_LIST = "rucard/servicos/restaurants"
        const val NETWORK_ENDPOINT_FETCH_RESTAURANT_MENU = "rucard/servicos/menu"

        const val NETWORK_URL_CUSTOM_RESTAURANT_LIST = "https://firebasestorage.googleapis.com/v0/b/e-bandejao-usp-beta.appspot.com/o/usp-restaurant-list.json"

        const val NETWORK_BASE_LIBRARY = "https://wssibi.uspdigital.usp.br/"
        const val NETWORK_ENDPOINT_LIBRARY_MAIN = "mobile/WExecute.php"
        const val NETWORK_ENDPOINT_LIBRARY_SEARCH = "mobile/WSearch.php"
        const val NETWORK_ENDPOINT_LIBRARY_LIBRARIES = "mobile/WLibrary.php"


        // Profile Picture File Storage
        const val FILE_ID_PROFILE_PIC = 200
        const val FILE_NAME_PROFILE_PIC = "profile_pic"

        // Work Manager
        const val WORKER_UPDATE_TOKEN_ID = "worker_update_token"
        //const val WORKER_UPDATE_TOKEN_REPEAT_INTERVAL_DAYS: Long = 1
        const val WORKER_UPDATE_TOKEN_START_HOUR_REQUEST = 1 // 03h - 11h -> 8h
        const val WORKER_UPDATE_TOKEN_END_HOUR_REQUEST = 5
        const val WORKER_UPDATE_TOKEN_REQUEST_INTERVAL = WORKER_UPDATE_TOKEN_END_HOUR_REQUEST - WORKER_UPDATE_TOKEN_START_HOUR_REQUEST
        const val WORKER_UPDATE_TOKEN_INITIAL_RETRY_DELAY_MINUTES: Long = 10
        const val WORKER_UPDATE_TOKEN_MAX_INACTIVE_APP_DAYS = 14

        const val SHARED_PREFS_NAME = "app_shared_prefs"
        const val SHARED_PREFS_APP_LAST_USE = "app_last_used"
        const val SHARED_PREFS_APP_LAST_FETCH_RESTAURANT_LIST = "app_last_fetch_restaurant_list"
        const val SHARED_PREFS_APP_LAST_FETCH_RESTAURANT = "app_last_fetch_restaurant"
        const val SHARED_PREFS_APP_ACCOUNT_BALANCE = "app_account_balance"

        // Shortcuts
        const val SHORTCUT_QRCODE_INTENT_DATA = "action_qrcode"
        const val SHORTCUT_BARCODE_INTENT_DATA = "action_barcode"
        const val SHORTCUT_MAX_OPEN_DELAY = 1000 * 3

        // Network Bound Resource
        const val NETWORK_MAX_RETRY_ATTEMPTS = 37

        //Login
        const val LOGIN_RETRY_DELAY_MILLIS: Long = 5000

        const val DEFAULT_BANDEJAO_ID = 2

        const val FRAG_ARG_DAILY_MENU = "daily_menu"
    }
}