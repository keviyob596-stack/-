package com.example.data.games

data class AutobusLetterData(
    val letter: Char,
    val boys: List<String>,
    val girls: List<String>,
    val animals: List<String>,
    val countries: List<String>,
    val objects: List<String>,
    val plants: List<String>,
    val foods: List<String>
)

object AutobusDictionary {
    val supportedLetters = listOf('أ', 'ب', 'ت', 'ج', 'د', 'ر', 'س', 'ع', 'م', 'ن', 'ي')

    val dictionary: Map<Char, AutobusLetterData> = mapOf(
        'أ' to AutobusLetterData(
            letter = 'أ',
            boys = listOf("أحمد", "أمير", "أيمن", "أسامة", "إبراهيم", "أكرم"),
            girls = listOf("أميرة", "أسماء", "آية", "أمل", "إيمان", "أروى"),
            animals = listOf("أسد", "أرنب", "أفعى", "إوزة", "أخطبوط"),
            countries = listOf("أمريكا", "ألمانيا", "أستراليا", "الأردن", "إسبانيا"),
            objects = listOf("إبرة", "أريكة", "إبريق", "أنبوب", "إطار"),
            plants = listOf("أرز", "أناناس", "أفوكادو", "أثل"),
            foods = listOf("أرز باللبن", "أم علي", "إندومي", "أومليت")
        ),
        'ب' to AutobusLetterData(
            letter = 'ب',
            boys = listOf("باسم", "بلال", "بدر", "بسام", "بهاء"),
            girls = listOf("بسمة", "بتول", "بثينة", "بلقيس", "بشائر"),
            animals = listOf("بقرة", "بطة", "بطريق", "بومة", "باندا"),
            countries = listOf("برازيل", "بلجيكا", "بريطانيا", "بحرين", "بولندا"),
            objects = listOf("باب", "برميل", "بطانية", "بندقية", "بوصلة"),
            plants = listOf("بطيخ", "بصل", "برتقال", "بقدونس", "بازلاء"),
            foods = listOf("بيتزا", "برجر", "بامية", "بسطرمة", "بسبوسة")
        ),
        'ت' to AutobusLetterData(
            letter = 'ت',
            boys = listOf("تامر", "طارق", "تحسين", "توفيق"),
            girls = listOf("تقى", "تهاني", "تسنيم", "تمارا", "تغريد"),
            animals = listOf("تمساح", "تيس", "تونة", "تنين"),
            countries = listOf("تونس", "تركيا", "تايلاند", "تشيلي"),
            objects = listOf("تلفاز", "تاج", "تلفون", "تنورة", "تابوت"),
            plants = listOf("تفاح", "تين", "توت", "تمر", "تبغ"),
            foods = listOf("تبولة", "ترمس", "تورتة", "تونا")
        ),
        'ج' to AutobusLetterData(
            letter = 'ج',
            boys = listOf("جمال", "جاسر", "جهاد", "جابر", "جعفر"),
            girls = listOf("جميلة", "جنى", "جودي", "جواهر", "جيهان"),
            animals = listOf("جمل", "جرو", "جراد", "جاموس"),
            countries = listOf("جزائر", "جورجيا", "جيبوتي", "جامايكا"),
            objects = listOf("جرس", "جريدة", "جسر", "جراب", "جورب"),
            plants = listOf("جزر", "جوافة", "جرجير", "جوز"),
            foods = listOf("جبنة", "جمبري", "جريش", "جلاش")
        ),
        'د' to AutobusLetterData(
            letter = 'د',
            boys = listOf("داود", "درويش", "دياب", "داني"),
            girls = listOf("داليا", "دينا", "دعاء", "دانة", "درة"),
            animals = listOf("دب", "ديك", "درفيل", "ديدان"),
            countries = listOf("دنمارك", "دبي", "دومينيكان"),
            objects = listOf("دفتر", "دولاب", "دبوس", "دراجة", "دلو"),
            plants = listOf("دوار الشمس", "دردار", "دوم"),
            foods = listOf("دجاج مشوي", "دونات", "دولمة")
        ),
        'ر' to AutobusLetterData(
            letter = 'ر',
            boys = listOf("رامي", "رضا", "رائد", "رشيد", "راكان"),
            girls = listOf("رانيا", "ريم", "رشا", "روان", "رحمة"),
            animals = listOf("راكون", "رنة", "ربيان"),
            countries = listOf("روسيا", "رومانيا", "رواندا"),
            objects = listOf("راديو", "ريشة", "رمح", "رسالة"),
            plants = listOf("رمان", "ريحان", "رشاد"),
            foods = listOf("رقاق", "رز معمر", "رافيولي")
        ),
        'س' to AutobusLetterData(
            letter = 'س',
            boys = listOf("سامي", "سعد", "سمير", "سالم", "سليمان"),
            girls = listOf("سارة", "سلمى", "سحر", "سهام", "سمية"),
            animals = listOf("سلحفاة", "سمكة", "سنجاب", "سلطعون"),
            countries = listOf("سعودية", "سوريا", "سودان", "سويد"),
            objects = listOf("ساعة", "سكين", "سيارة", "سرير", "سفينة"),
            plants = listOf("سبانخ", "سمسم", "سدر"),
            foods = listOf("سوشي", "سمبوسة", "سلطة", "سجق")
        ),
        'م' to AutobusLetterData(
            letter = 'م',
            boys = listOf("محمد", "محمود", "مصطفى", "ماجد", "مراد"),
            girls = listOf("مريم", "منى", "مها", "منال", "ميادة"),
            animals = listOf("ماعز", "مهر", "مدرع"),
            countries = listOf("مصر", "مغرب", "ماليزيا", "مكسيك"),
            objects = listOf("مفتاح", "مكتب", "مقص", "مروحة", "مسطرة"),
            plants = listOf("موز", "مانجو", "مشمش", "ملوخية"),
            foods = listOf("ملوخية بالأرانب", "محشي", "مكرونة بشاميل", "مسقعة")
        ),
        'ن' to AutobusLetterData(
            letter = 'ن',
            boys = listOf("نادر", "نبيل", "ناجي", "ناصر", "نور"),
            girls = listOf("نور", "نهى", "ندى", "نادية", "نجوى"),
            animals = listOf("نمر", "نسر", "نحلة", "نملة", "نعامة"),
            countries = listOf("نيجيريا", "نرويج", "نمسا", "نيبال"),
            objects = listOf("نظارة", "نافذة", "نقود", "ناي"),
            plants = listOf("نعناع", "نخيل", "نرجس"),
            foods = listOf("ناجتس", "نودلز", "نيوتيلا")
        )
    )

    fun getRandomLetter(): Char {
        return supportedLetters.random()
    }

    fun getBotAnswer(letter: Char, category: String, botDifficulty: String): String {
        val data = dictionary[letter] ?: return ""
        val list = when (category) {
            "ولد" -> data.boys
            "بنت" -> data.girls
            "حيوان" -> data.animals
            "بلد" -> data.countries
            "جماد" -> data.objects
            "نبات" -> data.plants
            "أكل" -> data.foods
            else -> emptyList()
        }
        if (list.isEmpty()) return ""

        return when (botDifficulty) {
            "EASY" -> {
                // Easy bot sometimes misses or gives answer with delay
                if (Math.random() < 0.3) "" else list.random()
            }
            "MEDIUM" -> {
                if (Math.random() < 0.15) "" else list.random()
            }
            else -> { // HARD
                list.random()
            }
        }
    }

    fun validateAnswer(letter: Char, category: String, answer: String): Boolean {
        val clean = answer.trim()
        if (clean.isEmpty()) return false
        val firstChar = clean.first()
        // Check if starts with the letter or 'ال' + letter
        val startsWithLetter = firstChar == letter || 
            (clean.startsWith("ال") && clean.length > 2 && clean[2] == letter) ||
            (letter == 'أ' && (firstChar == 'ا' || firstChar == 'إ' || firstChar == 'آ'))
        
        return startsWithLetter
    }
}
