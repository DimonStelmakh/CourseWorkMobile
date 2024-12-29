package com.example.courseworkmobile.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.example.courseworkmobile.data.local.NewsDatabase
import com.example.courseworkmobile.data.local.NewsDao
import com.example.courseworkmobile.data.local.UserDao
import com.example.courseworkmobile.data.local.NotificationDao
import com.example.courseworkmobile.data.local.ArticleKeywordDao
import com.example.courseworkmobile.data.local.CategoryDao
import com.example.courseworkmobile.data.local.CategoryEntity
import com.example.courseworkmobile.data.local.FavoriteDao
import com.example.courseworkmobile.data.local.KeywordSubscriptionDao
import com.example.courseworkmobile.data.local.LanguageDao
import com.example.courseworkmobile.data.local.LanguageEntity
import com.example.courseworkmobile.data.local.NewsEntity
import com.example.courseworkmobile.data.local.NotificationEntity
import com.example.courseworkmobile.data.local.PasswordManager
import com.example.courseworkmobile.data.local.UserEntity
import com.example.courseworkmobile.data.local.ArticleKeyword
import com.example.courseworkmobile.data.local.VerificationRequestDao

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideNewsDatabase(@ApplicationContext context: Context): NewsDatabase {
        return Room.databaseBuilder(
            context,
            NewsDatabase::class.java,
            "news_database"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    CoroutineScope(Dispatchers.IO).launch {
                        val database = provideNewsDatabase(context)

                        val categories = listOf(
                            "Політика", "Економіка", "Природа", "Спорт", "Лайфстайл"
                        ).map { CategoryEntity(name = it) }

                        categories.forEach {
                            database.categoryDao.insertCategory(it)
                        }

                        // сідування мов
                        val languages = listOf(
                            "Українська", "English"
                        ).map { LanguageEntity(name = it) }

                        languages.forEach {
                            database.languageDao.insertLanguage(it)
                        }

                        // сідування тестової новини
                        database.newsDao.insertNews(
                            NewsEntity(
                                title = "WSJ: Трамп сказав Макрону і Зеленському, що припинення війни можна контролювати європейські війська",
                                content = "За інформацією видання, Трамп сказав Зеленському та Макрону, що це буде справедливо по відношенню до США",
                                imageUrl = "https://www.internationalaffairs.org.au/wp-content/uploads/2024/12/Paris_Hosted_a_Trilateral_Meeting_Between_Volodymyr_Zelenskyy_Emmanuel_Macron_and_Donald_Trump_on_7_December_2024_-_2.jpg",
                                timestamp = System.currentTimeMillis(),
                                author = "BBC News Ukraine",
                                categoryId = 1,
                                views = 815,
                                languageId = 1
                            )
                        )

                        database.newsDao.insertNews(
                            NewsEntity(
                                title = "За підсумками 2024 року очікується зростання ВВП України на 4%, – Мінекономіки",
                                content = "Перша віцепрем’єр-міністерка – міністерка економіки Юлія Свириденко поінформувала, що за підсумками 2024 року очікується  зростання ВВП України на 4%, повідомляє пресслужба Мінекономіки. \n" +
                                        "\n" +
                                        "“Із загального 4%-го зростання, реалізація політики розвитку українських виробників “Зроблено в Україні” цьогоріч забезпечила 0,64%, або більш як 88 млрд грн – свідчать розрахунки Мінекономіки. Переробна промисловість увійшла у лідери за сплатою податків. За результатами трьох кварталів 2024 року на неї припадає 16,7% від загального збору до Зведеного бюджету України. А зростання сплати податків за перші 9 місяців склало 19,6% у порівнянні з аналогічним періодом минулого року”, – зауважила Юлія Свириденко у колонці для “Інтерфакс-Україна”.\n" +
                                        "\n" +
                                        "Цього року уряд зосередився на трьох ключових напрямах для збільшення частки переробної промисловості у ВВП: розвиток виробництва, залучення інвестицій у реальний сектор, сприяння несировинному експорту. \n" +
                                        "\n" +
                                        "Формувати попит на українські товари з боку держави та громад допомагає політика локалізації у публічних закупівлях. З 1 січня 2025 року вимога частки місцевої складової зросте до 25%. \n" +
                                        "\n" +
                                        "Програма “Шкільний автобус” дозволила громадам закупити 1 000 автобусів за два роки, забезпечивши роботою працівників українських автобусних заводів і понад 200 підрядників. \n" +
                                        "\n" +
                                        "Для бізнесу діють програми компенсацій 15% та 25% за купівлю української техніки. Вже майже 150 виробників скористались ними. \n" +
                                        "\n" +
                                        "Пільгова іпотека єОселя сприяє зростанню попиту на будівельні матеріали. Цього року її переорієнтували на первинний ринок нерухомості.\n" +
                                        "\n" +
                                        "“Виробництво будматеріалів у першому півріччі 2024 року зросло на 37,1%, а частка іпотек на первинному ринку подвоїлась. Програма також сприяє детінізації економіки. У першому півріччі забудовники сплатили 1,6 млрд грн податків, що у 2,2 раза більше, ніж за аналогічний період торік”, – зазначила міністерка економіки.\n" +
                                        "\n" +
                                        "Попит громадян на українські товари стимулює програма “Національний кешбек”. За підрахунками Retail Association of Ukraine, у перші місяці її реалізації продажі певних продуктів зросли у кількості на 9,5%.",
                                imageUrl = "https://i.lb.ua/017/00/676d37ab450ae.jpeg",
                                timestamp = System.currentTimeMillis(),
                                author = "РБК-Україна",
                                categoryId = 2,
                                views = 944,
                                languageId = 1
                            )
                        )

                        database.articleKeywordDao.insertKeywords(
                            listOf(
                                ArticleKeyword(
                                    articleId = 2,
                                    keyword = "Свириденко"
                                ),
                                ArticleKeyword(
                                    articleId = 2,
                                    keyword = "ВВП"
                                ),
                                ArticleKeyword(
                                    articleId = 2,
                                    keyword = "Україна"
                                ),
                                ArticleKeyword(
                                    articleId = 1,
                                    keyword = "Трамп"
                                ),
                                ArticleKeyword(
                                    articleId = 1,
                                    keyword = "Зеленський"
                                ),
                                ArticleKeyword(
                                    articleId = 1,
                                    keyword = "Макрон"
                                ),
                                ArticleKeyword(
                                    articleId = 1,
                                    keyword = "Україна"
                                )
                            )
                        )


                        // сідування тестових сповіщень
                        database.notificationDao.insertNotification(
                            NotificationEntity(
                                message = "Настав час самому створити новину! Подавайте запит на верифікацію як автор новин у себе в профілі і наші менеджери зв'яжуться з Вами за Вашим контактним номером!",
                                timestamp = System.currentTimeMillis(),
                                recipientId = 1,
                                isRead = true
                            )
                        )

                        database.notificationDao.insertNotification(
                            NotificationEntity(
                                message = "Найсвіжіша підбірка новин на головній сторінці! Дізнайтесь про все першими!",
                                timestamp = System.currentTimeMillis(),
                                recipientId = 1,
                                isRead = false
                            )
                        )

                        // сідування тестових користувачів
                        val passwordManager = PasswordManager()
                        var hashedPassword = passwordManager.hashPassword("password123")
                        database.userDao.insertUser(
                            UserEntity(
                                username = "besteditor333",
                                firstname = "Mr.",
                                surname = "Editor",
                                phone = "+380971122333",
                                email = "besteditor@gmail.com",
                                password = hashedPassword,
                                isVerified = true,
                                isAdmin = false
                            )
                        )
                        hashedPassword = passwordManager.hashPassword("123123123")
                        database.userDao.insertUser(
                            UserEntity(
                                username = "newuser",
                                firstname = "New",
                                surname = "User",
                                phone = "+380989988777",
                                email = "newuser@gmail.com",
                                password = hashedPassword,
                                isVerified = false,
                                isAdmin = false
                            )
                        )
                        hashedPassword = passwordManager.hashPassword("admin1")
                        database.userDao.insertUser(
                            UserEntity(
                                username = "admin1",
                                firstname = "Admin",
                                surname = "User",
                                phone = "+380970011222",
                                email = "admin@gmail.com",
                                password = hashedPassword,
                                isVerified = true,
                                isAdmin = true
                            )
                        )
                    }
                }
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsDao(database: NewsDatabase): NewsDao = database.newsDao

    @Provides
    @Singleton
    fun provideUserDao(database: NewsDatabase): UserDao = database.userDao

    @Provides
    @Singleton
    fun provideNotificationDao(database: NewsDatabase): NotificationDao = database.notificationDao

    @Provides
    @Singleton
    fun provideVerificationRequestDao(database: NewsDatabase): VerificationRequestDao = database.verificationRequestDao

    @Provides
    @Singleton
    fun provideCategoryDao(database: NewsDatabase): CategoryDao = database.categoryDao

    @Provides
    @Singleton
    fun provideLanguageDao(database: NewsDatabase): LanguageDao = database.languageDao

    @Provides
    @Singleton
    fun provideFavoriteDao(database: NewsDatabase): FavoriteDao = database.favoriteDao

    @Provides
    @Singleton
    fun provideArticleKeywordDao(database: NewsDatabase): ArticleKeywordDao = database.articleKeywordDao

    @Provides
    @Singleton
    fun provideKeywordSubscriptionDao(database: NewsDatabase): KeywordSubscriptionDao = database.keywordSubscriptionDao
}