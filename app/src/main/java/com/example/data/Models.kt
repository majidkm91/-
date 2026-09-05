package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_config")
data class UserConfigEntity(
    @PrimaryKey val id: Int = 1,
    val quitTimestamp: Long = System.currentTimeMillis(),
    val cigsPerDay: Int = 20,
    val costPerPack: Long = 60000L,
    val packSize: Int = 20,
    val cravingResistedCount: Int = 0
)

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String,
    val unlockedAt: Long = 0L
)

data class HealthStage(
    val id: String,
    val hours: Double,
    val title: String,
    val desc: String,
    val iconEmoji: String
)

data class BadgeDefinition(
    val id: String,
    val title: String,
    val desc: String,
    val icon: String,
    val reqHours: Double? = null,
    val reqCigs: Int? = null,
    val reqMoney: Long? = null,
    val reqCravingResisted: Int? = null
)

object AppConstants {
    val quotes = listOf(
        "هر ثانیه‌ای که نمی‌کشی، بدنت در حال ترمیم سلول‌های خودشه.",
        "وسوسه یک مهمان ناخوانده است؛ بهش چای تعارف نکن، خودش میره.",
        "کنترل زندگی‌ات در دستان توست، نه در یک تکه کاغذ و توتون.",
        "ریه‌هایت از تو ممنون‌اند که دوباره اجازه نفس کشیدن به آنها دادی.",
        "پیروزی یعنی غلبه بر صدای درون که می‌گوید «فقط همین یکی!»",
        "با هر نخ نکشیدن، ۱۱ دقیقه به عمر طلایی خود اضافه می‌کنی.",
        "آزادی از نیکوتین بهترین هدیه‌ای است که به جسم و جانت می‌دهی."
    )

    val healthStages = listOf(
        HealthStage(
            id = "stage_20m",
            hours = 0.33,
            title = "۲۰ دقیقه بعد",
            desc = "نبض و فشار خون به حالت عادی برمی‌گردند.",
            iconEmoji = "❤️"
        ),
        HealthStage(
            id = "stage_8h",
            hours = 8.0,
            title = "۸ ساعت بعد",
            desc = "میزان مونوکسید کربن در خون نصف شده و سطح اکسیژن افزایش می‌یابد.",
            iconEmoji = "🩸"
        ),
        HealthStage(
            id = "stage_24h",
            hours = 24.0,
            title = "۲۴ ساعت بعد",
            desc = "خطر حمله قلبی رو به کاهش می‌گذارد و ریه‌ها شروع به پاکسازی می‌کنند.",
            iconEmoji = "🫁"
        ),
        HealthStage(
            id = "stage_48h",
            hours = 48.0,
            title = "۴۸ ساعت بعد",
            desc = "نیکوتین از بدن پاک می‌شود؛ پایانه‌های عصبی دوباره رشد کرده و حس بویایی و چشایی تیزتر می‌شوند.",
            iconEmoji = "👃"
        ),
        HealthStage(
            id = "stage_72h",
            hours = 72.0,
            title = "۷۲ ساعت بعد",
            desc = "برونش‌ها بازتر شده و تنفس بسیار راحت‌تر و سبک‌تر می‌شود.",
            iconEmoji = "🌬️"
        ),
        HealthStage(
            id = "stage_2w",
            hours = 336.0,
            title = "۲ هفته بعد",
            desc = "جریان خون در عضلات بهتر شده و بازده ریه تا ۳۰٪ بالا می‌رود.",
            iconEmoji = "🏃"
        ),
        HealthStage(
            id = "stage_1m",
            hours = 720.0,
            title = "۱ ماه بعد",
            desc = "سرفه‌ها کمتر شده، تنگی نفس رفع می‌شود و انرژی روزانه دوچندان می‌گردد.",
            iconEmoji = "⚡"
        ),
        HealthStage(
            id = "stage_1y",
            hours = 8760.0,
            title = "۱ سال بعد",
            desc = "خطر ابتلا به بیماری عروق کرونر قلب نصف افراد سیگاری می‌شود.",
            iconEmoji = "🏆"
        )
    )

    val allBadges = listOf(
        BadgeDefinition(
            id = "b_day1",
            title = "قدم اول",
            desc = "۲۴ ساعت رهایی و پاکی کامل",
            icon = "🌱",
            reqHours = 24.0
        ),
        BadgeDefinition(
            id = "b_day3",
            title = "عبور از جهنم",
            desc = "۳ روز پاکی (خروج کامل نیکوتین)",
            icon = "🔥",
            reqHours = 72.0
        ),
        BadgeDefinition(
            id = "b_week1",
            title = "قهرمان یک هفته‌ای",
            desc = "۷ روز استقامت شگفت‌انگیز",
            icon = "🛡️",
            reqHours = 168.0
        ),
        BadgeDefinition(
            id = "b_month1",
            title = "تولد دوباره",
            desc = "یک ماه تمام بدون دود و سم",
            icon = "🌟",
            reqHours = 720.0
        ),
        BadgeDefinition(
            id = "b_cigs100",
            title = "صدتایی‌ها",
            desc = "جلوگیری از ۱۰۰ نخ سیگار کشنده",
            icon = "💎",
            reqCigs = 100
        ),
        BadgeDefinition(
            id = "b_rich",
            title = "قلک هوشمند",
            desc = "پس‌انداز ۵۰۰ هزار تومان پول نقد",
            icon = "👑",
            reqMoney = 500000L
        ),
        BadgeDefinition(
            id = "b_month3",
            title = "اراده پولادین",
            desc = "سه ماه تمام (۹۰ روز) پاکی مطلق",
            icon = "🦅",
            reqHours = 2160.0
        ),
        BadgeDefinition(
            id = "b_craving5",
            title = "محافظ تنفس",
            desc = "مهار موفق ۵ موج وسوسه شدید",
            icon = "🧘‍♂️",
            reqCravingResisted = 5
        ),
        BadgeDefinition(
            id = "b_cigs1000",
            title = "استاد پاکی",
            desc = "جلوگیری از دود شدن ۱۰۰۰ نخ سیگار",
            icon = "🎖️",
            reqCigs = 1000
        )
    )
}
