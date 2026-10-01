package com.example.data.repository

import com.example.data.local.CartDao
import com.example.data.local.CartItemEntity
import com.example.data.local.PantryDao
import com.example.data.local.PantryItemEntity
import com.example.data.local.SavedRecipeDao
import com.example.data.local.SavedRecipeEntity
import com.example.data.model.ChefRecipe
import com.example.data.model.CookingStep
import com.example.data.model.RecipeCategory
import com.example.data.model.RecipeIngredient
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ChefRepository(
    private val cartDao: CartDao,
    private val savedRecipeDao: SavedRecipeDao,
    private val pantryDao: PantryDao
) {
    val allCartItems: Flow<List<CartItemEntity>> = cartDao.getAllCartItems()
    val savedRecipeIds: Flow<List<String>> = savedRecipeDao.getSavedRecipeIds()
    val pantryItems: Flow<List<PantryItemEntity>> = pantryDao.getAllPantryItems()

    suspend fun toggleFavorite(recipeId: String) {
        val isSaved = savedRecipeDao.isRecipeSaved(recipeId) > 0
        if (isSaved) {
            savedRecipeDao.removeRecipe(recipeId)
        } else {
            savedRecipeDao.saveRecipe(SavedRecipeEntity(recipeId))
        }
    }

    suspend fun addIngredientsToCart(recipe: ChefRecipe) {
        val entities = recipe.ingredients
            .filter { !it.isPantryStaple }
            .map { ingredient ->
                CartItemEntity(
                    id = UUID.randomUUID().toString(),
                    recipeId = recipe.id,
                    recipeTitle = recipe.shortTitle,
                    name = ingredient.name,
                    amount = ingredient.amount,
                    aisle = ingredient.aisle,
                    price = ingredient.estimatedPrice,
                    isChecked = false,
                    supermarket = ingredient.supermarketBrand
                )
            }
        cartDao.insertItems(entities)
    }

    suspend fun toggleCartItemChecked(item: CartItemEntity) {
        cartDao.updateItem(item.copy(isChecked = !item.isChecked))
    }

    suspend fun deleteCartItem(id: String) {
        cartDao.deleteItemById(id)
    }

    suspend fun clearCheckedCartItems() {
        cartDao.clearCheckedItems()
    }

    suspend fun clearAllCartItems() {
        cartDao.clearAll()
    }

    suspend fun addCustomCartItem(name: String, price: Double, aisle: String) {
        val entity = CartItemEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            amount = "1 יח'",
            aisle = aisle,
            price = price,
            isChecked = false
        )
        cartDao.insertItem(entity)
    }

    fun getAllRecipes(): List<ChefRecipe> = sampleRecipes

    fun getRecipeById(id: String): ChefRecipe? = sampleRecipes.find { it.id == id }

    companion object {
        val sampleRecipes = listOf(
            ChefRecipe(
                id = "salmon-pan-seared",
                title = "פילה סלמון צרוב במחבת ברוטב חמאת לימון ושום על מצע שעועית ירוקה מוקפצת",
                shortTitle = "פילה סלמון צרוב בחמאת לימון",
                description = "מנת שף עילית אלגנטית ומאוזנת: עור פריך ומתפצפץ עם בשר סלמון עסיסי ורך, מלווה ברוטב חמאה הולנדית, לימון טרי ושמיר, על מצע שעועית ירוקה פריכה.",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDcvJxf0Pe9Xop_9HE_ACbuTQi-vOLz1P07PNlh9tExL2axIq3hotMNyFuzmNKIUcsf9moL9YEq7vS8fq814mpeW-Kpm34zqQGuqtfyFd4PdMwSeTMSskDgHp5xc2ylkAfSjXrgMR7bjtFWSPxRasA-qTSH5dsYQ8Zp4Yf3MphvjzGUNl5wH-rfSRf44lWGfInqRoXcffkQwEQWGuij8Mx9tY2ssF8UTlZc84MA_4ECzfTO6DpAaneDoQ",
                priceSupermarket = 79.40,
                originalPrice = 104.00,
                savingsBadge = "חיסכון מובטח",
                remainingBudget = 5.60,
                timeMinutes = 20,
                difficulty = "רמת שף: קל",
                servings = 2,
                category = RecipeCategory.BEST_VALUE,
                dietaryBadge = "ללא גלוטן",
                isChefRecommended = true,
                supermarketIngredientsCount = 4,
                pantryStaplesCount = 2,
                chefTip = "סלמון השבוע במבצע בשופרסל, ניצלנו את המחיר כדי לתת לכם מנת מסעדה בפחות מ-₪40 למנה!",
                restaurantPriceEstimate = 220.0,
                ingredients = listOf(
                    RecipeIngredient("ing-1", "פילה סלמון טרי נקי מעצמות", "400 גרם", "דגים ובשר טרי", 49.90, false, "שופרסל Fresh"),
                    RecipeIngredient("ing-2", "שעועית ירוקה עדינה קפואה", "400 גרם", "קפואים וירקות", 12.90, false, "שופרסל"),
                    RecipeIngredient("ing-3", "חמאה הולנדית איכותית", "100 גרם", "מוצרי חלב", 8.90, false, "תנובה"),
                    RecipeIngredient("ing-4", "לימון עסיסי מובחר", "1 יח'", "פירות וירקות", 3.20, false, "שופרסל"),
                    RecipeIngredient("ing-5", "שמיר טרי שטוף ורענן", "1 צרור", "פירות וירקות", 4.50, false, "שופרסל"),
                    RecipeIngredient("ing-6", "שום כתוש טרי", "3 שיניים", "תבלינים ומזווה", 0.0, true),
                    RecipeIngredient("ing-7", "מלח ים גס ושמן זית כתית מעולה", "לפי הטעם", "תבלינים ומזווה", 0.0, true)
                ),
                steps = listOf(
                    CookingStep(1, "הכנת הדג וייבוש", "מייבשים את פילה הסלמון משני צדדיו בנייר סופג עד שהוא יבש לחלוטין. חורצים 2 חריצים עדינים בעור ומתבלים במלח ים ופלפל שחור גרוס.", "עור יבש הוא המפתח לעור פריך וקראנצ'י!"),
                    CookingStep(2, "צריבת העור לקראנץ' מושלם", "מחממים מחבת כבדה עם כף שמן זית על אש בינונית-גבוהה. מניחים את הסלמון כשהעור כלפי מטה, ולוחצים בעדינות עם תרווד במשך 20 שניות.", "אל תזיזו את הדג בדקות הראשונות, הוא ישתחרר לבד כשהעור יהיה מוכן.", 240, "אש בינונית-גבוהה"),
                    CookingStep(3, "הפיכה ויצירת רוטב החמאה", "הופכים בזהירות לצד השני, מנמיכים מעט את האש ומוסיפים למחבת את קוביות החמאה, השום הכתוש ומיץ הלימון. בעזרת כף משקים ברציפות את הסלמון ברוטב החמאה המבעבע.", "טכניקת Basting צרפתית קלאסית שמעניקה לדג עסיסיות נדירה.", 120, "אש נמוכה"),
                    CookingStep(4, "הקפצת שעועית ירוקה", "במחבת מקבילה (או לאחר הוצאת הדג למנוחה), מקפיצים את השעועית הירוקה על אש גבוהה עם טיפת שמן זית, מלח וקליפת לימון מגוררת.", "שמרו על השעועית קראנצ'ית ומבריקה בצבע ירוק חי.", 180, "אש גבוהה"),
                    CookingStep(5, "צלחות שף והגשה", "מניחים ערימת שעועית ירוקה במרכז צלחת הגשה רחבה. מניחים מעליה בעדינות את פילה הסלמון. יוצקים בנדיבות מרוטב החמאה-לימון-שום ומקשטים בשמיר טרי.", "הגישו מיד לצד כוס יין לבן צונן.")
                )
            ),
            ChefRecipe(
                id = "pappardelle-mushrooms",
                title = "פפרדלה ארטישוק, פטריות יער ופרמז׳ן בשמן זית ושום",
                shortTitle = "פפרדלה ארטישוק ופטריות יער",
                description = "סרטי פסטה רחבים בעבודת מסורתית מתערבלים עם פטריות צרובות עד השחמה עמוקה, לבבות ארטישוק איטלקי, שום זהוב ועלי מרווה, מנוקדים בשבבי פרמז'ן מיושן.",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDnJdyuJ_eSRj-dQXKolexTgyXkP0GDpElOsME_u8M0zGIr0iL06PtT9Ww8oVD0cA8n2Mu40dh9DGIUo_o_m6ZHJ31msGKpxvbhXXrf8Jfby32vat9Bt5utdX41Z0pBvZiod9ZvP6p4kgjpXrGPwdVeAk_sIutEPntrYOJK0VoNg4PZ3ZiUfVNT4SJa3rT1QcsE5DD2q6RYAy0zba8ND4NoRCVbdHx65mqW2Sm84EwM0j4dmO5ZLwGiZg",
                priceSupermarket = 54.20,
                originalPrice = 85.00,
                savingsBadge = "חיסכון של ₪30.80!",
                remainingBudget = 30.80,
                timeMinutes = 25,
                difficulty = "קל-בינוני",
                servings = 2,
                category = RecipeCategory.FAST_PREP,
                dietaryBadge = "צמחוני",
                isChefRecommended = false,
                supermarketIngredientsCount = 4,
                pantryStaplesCount = 3,
                chefTip = "אל תשפכו את מי הפסטה! חצי כוס ממי הבישול עשירים בעמילן יעניקו לרוטב מרקם קטיפתי מושלם בלי שמנת.",
                restaurantPriceEstimate = 160.0,
                ingredients = listOf(
                    RecipeIngredient("ing-21", "פסטה פפרדלה איטלקית", "500 גרם", "יבשים ופסטה", 14.90, false, "ברילה"),
                    RecipeIngredient("ing-22", "סלסלת פטריות שמפיניון ופורטובלו", "250 גרם", "פירות וירקות", 12.90, false, "שופרסל Fresh"),
                    RecipeIngredient("ing-23", "לבבות ארטישוק מובחרים", "1 צנצנת", "שימורים ומעדנייה", 13.50, false, "שופרסל"),
                    RecipeIngredient("ing-24", "גבינת פרמז'ן גרנה פדנו מגוררת", "100 גרם", "גבינות ומעדנייה", 12.90, false, "מחלבות גד"),
                    RecipeIngredient("ing-25", "שמן זית כתית מעולה", "3 כפות", "תבלינים ומזווה", 0.0, true),
                    RecipeIngredient("ing-26", "שיני שום פרוסות דק", "4 שיניים", "תבלינים ומזווה", 0.0, true),
                    RecipeIngredient("ing-27", "פלפל שחור גרוס ומלח", "לפי הטעם", "תבלינים ומזווה", 0.0, true)
                ),
                steps = listOf(
                    CookingStep(1, "בישול הפסטה", "מרתיחים סיר גדול עם מים ומלח ביד רחבה. מבשלים את הפפרדלה דקה פחות מהוראות היצרן לקבלת אל-דנטה אמיתי. שומרים חצי כוס ממי הבישול!", "מים מלוחים כמו הים מעניקים לפסטה את הטעם האיטלקי הנכון.", 540, "רתיחה"),
                    CookingStep(2, "צריבת הפטריות והשום", "במחבת רחבה מחממים שמן זית על אש גבוהה. מניחים את הפטריות הפרוסות מבלי להזיז דקה וחצי עד השחמה עמוקה. מוסיפים את השום הפרוס ולבבות הארטישוק.", "חום גבוה צורב את הפטריות במקום להוציא מהן נוזלים.", 200, "אש גבוהה"),
                    CookingStep(3, "איחוד הפסטה ואמולסיה", "מעבירים את הפפרדלה ישירות מהסיר למחבת, יוצקים 4 כפות ממי הפסטה ומערבבים בתנועות הקפצה מהירות ליצירת רוטב מבריק שעוטף כל סרט פסטה.", "העמילן שבמים יוצר רוטב קרמי טבעי.", 90, "אש בינונית"),
                    CookingStep(4, "הגשה עם שפע פרמז'ן", "מעבירים לצלחות עמוקות, מפזרים בנדיבות שבבי פרמז'ן טרי, מטפטפים שמן זית איכותי ומסיימים בפלפל שחור גרוס גס.")
                )
            ),
            ChefRecipe(
                id = "tofu-shawarma-bowl",
                title = "קערת שווארמה טופו פריכה, טחינה הר ברכה, סלט שוק צבעוני וקרעי פיתה",
                shortTitle = "קערת שווארמה טופו וטחינה",
                description = "רצועות טופו זהובות ומתובלות בסומאק, כמון וכורכום, נצרבות לפריכות מושלמת במחבת, מונחות על קינואה או אורז לצד סלט שוק ישראלי קצוץ דק, טחינה הר ברכה וקרעי פיתה שרופים.",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCwazI6BHSR4eiFr5kM49Rs6eSFaoDZrawpZKdUy9v9PabHN3GbjGIZ-53E60a8V-sfv35Vm5ojWdPe0OG5mi9ZluFzjWHcOl2GMvRS5Aji612_72PTX4bzr_uYk9IFZvzad5HRTepjbX1c_fz3gt6ZcPBdUwZY0oCjeH_XUwXl7k7FKMFJXCW9YEd5vnZXsOe15su3ROcJ6GyHMfBc-fUDFFGj5NNpHvpv897kj16bUDl9HvjA_wlCSg",
                priceSupermarket = 48.90,
                originalPrice = 72.00,
                savingsBadge = "סופר חסכוני",
                remainingBudget = 36.10,
                timeMinutes = 18,
                difficulty = "רמת שף: קל",
                servings = 2,
                category = RecipeCategory.HEALTHY,
                dietaryBadge = "טבעוני עשיר בחלבון (28 גרם)",
                isChefRecommended = false,
                supermarketIngredientsCount = 4,
                pantryStaplesCount = 3,
                chefTip = "לפני הצריבה סחטו היטב את הטופו בנייר מגבת וחיתכו למקלות אחידים – זה הסוד למרקם פריך מבחוץ ונימוח מבפנים.",
                restaurantPriceEstimate = 140.0,
                ingredients = listOf(
                    RecipeIngredient("ing-31", "טופו טבעי ביו מובחר", "300 גרם", "מקרר ומזון בריאות", 11.90, false, "שופרסל גרין"),
                    RecipeIngredient("ing-32", "טחינה גולמית הר ברכה", "500 גרם", "תבלינים ומזווה", 16.90, false, "הר ברכה"),
                    RecipeIngredient("ing-33", "ירקות לסלט: מלפפונים, שרי, בצל סגול ופטרוזיליה", "1 מארז", "פירות וירקות", 14.50, false, "שופרסל Fresh"),
                    RecipeIngredient("ing-34", "פיתות עבודת יד", "מארז 5 יח'", "לחמים ומאפים", 5.60, false, "מאפיית הבית"),
                    RecipeIngredient("ing-35", "תבלין שווארמה, סומאק וכמון", "לפי הטעם", "תבלינים ומזווה", 0.0, true),
                    RecipeIngredient("ing-36", "מיץ לימון ושמן זית", "לפי הטעם", "תבלינים ומזווה", 0.0, true)
                ),
                steps = listOf(
                    CookingStep(1, "תיבול הטופו", "מייבשים את הטופו היטב, פורסים לרצועות דמויות שווארמה ומערבבים בקערה עם כף שמן זית, סומאק, כמון, כורכום, שום ומלח.", "תנו לתבלינים להיספג 3 דקות לפני המחבת.", 180),
                    CookingStep(2, "צריבת שווארמת הטופו", "מחממים מחבת טפלון עם מעט שמן. מוסיפים את הטופו וצורבים על חום גבוה כ-6 דקות תוך ערבוב מדי פעם עד שנוצרת מעטפת פריכה ושחומה.", "הטמפרטורה הגבוהה מקנה לטופו את אפקט הגריל.", 360, "אש גבוהה"),
                    CookingStep(3, "הכנת קרעי פיתה וסלט שוק", "קוצצים דק מלפפונים, עגבניות שרי, פטרוזיליה ובצל סגול. מתבלים בשמן זית, לימון ומלח. קורעים פיתה לחתיכות גסות וקולים קלות במחבת עם סומאק.", "סלט קצוץ דק דק מעניק רעננות מול חום השווארמה.", 180),
                    CookingStep(4, "הרכבת הקערה", "יוצקים טחינה הר ברכה בתחתית הקערה. מניחים ערימת שווארמת טופו פריכה, סלט שוק צבעוני וקרעי פיתה קלויים. מפזרים עוד סומאק מעל.")
                )
            ),
            ChefRecipe(
                id = "sirloin-steak-bistro",
                title = "מדליוני סינטה מיושנת ברוטב ציר בקר, טימין ושום לצד פירה קטיפתי",
                shortTitle = "מדליוני סינטה ופירה שף",
                description = "מנת ביסטרו צרפתי קלאסית: נתחי סינטה עגל מיושנים ומשוישים, צרובים בחמאה וטימין, עם רוטב מצומצם עשיר ופירה שף אוורירי.",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDcvJxf0Pe9Xop_9HE_ACbuTQi-vOLz1P07PNlh9tExL2axIq3hotMNyFuzmNKIUcsf9moL9YEq7vS8fq814mpeW-Kpm34zqQGuqtfyFd4PdMwSeTMSskDgHp5xc2ylkAfSjXrgMR7bjtFWSPxRasA-qTSH5dsYQ8Zp4Yf3MphvjzGUNl5wH-rfSRf44lWGfInqRoXcffkQwEQWGuij8Mx9tY2ssF8UTlZc84MA_4ECzfTO6DpAaneDoQ",
                priceSupermarket = 84.50,
                originalPrice = 118.00,
                savingsBadge = "דיוק תקציב מושלם",
                remainingBudget = 0.50,
                timeMinutes = 30,
                difficulty = "שף",
                servings = 2,
                category = RecipeCategory.GOURMET,
                dietaryBadge = "בשרי פרימיום",
                isChefRecommended = false,
                supermarketIngredientsCount = 4,
                pantryStaplesCount = 3,
                chefTip = "תנו לסטייק לנוח לפחות 4 דקות על קרש עץ לפני החיתוך – כל המיצים יינעלו בבשר.",
                restaurantPriceEstimate = 260.0,
                ingredients = listOf(
                    RecipeIngredient("ing-41", "מדליוני סינטה עגל טרי מיושן", "400 גרם", "קצביה ובשר טרי", 58.90, false, "קצבית שופרסל"),
                    RecipeIngredient("ing-42", "תפוחי אדמה גורמה לפירה (ראטה)", "800 גרם", "פירות וירקות", 9.90, false, "שופרסל Fresh"),
                    RecipeIngredient("ing-43", "חמאה אירופאית", "100 גרם", "מוצרי חלב", 8.90, false, "תנובה"),
                    RecipeIngredient("ing-44", "צרור טימין טרי ורוזמרין", "1 יח'", "פירות וירקות", 6.80, false, "שופרסל"),
                    RecipeIngredient("ing-45", "שיני שום, מלח ים ופלפל שחור", "לפי הטעם", "תבלינים ומזווה", 0.0, true)
                ),
                steps = listOf(
                    CookingStep(1, "הכנת הפירה הקטיפתי", "מבשלים תפוחי אדמה במים מומלחים עד שרכים לחלוטין. מועכים בעודם חמים עם חמאה ומעט מלח עד לקבלת מרקם משי.", "מעיכה בחום עם חמאה קרה מעניקה מרקם פירה כמו במסעדת מישלן.", 900),
                    CookingStep(2, "צריבת הסינטה", "מחממים מחבת ברזל כבדה עד לעישון קל. צורבים את מדליוני הסינטה 2 דקות מכל צד למידת עשייה מדיום מדויקת.", "חום מקסימלי שומר על עסיסיות הנתח.", 240, "אש גבוהה"),
                    CookingStep(3, "השקייה בחמאה וטימין", "מוסיפים חמאה, שיני שום מעוכות וענפי טימין. משקים את הבשר בחמאה החמה במשך דקה נוספת.", "הארומה של הטימין והשום חודרת ישירות לסינטה.", 60),
                    CookingStep(4, "מנוחה וחיתוך", "מעבירים לקרש חיתוך למנוחה של 4 דקות. פורסים למדליונים אלכסוניים ומגישים על תלולית פירה.", "מנוחת הנתח קריטית לשמירת המיצים.", 240)
                )
            ),
            ChefRecipe(
                id = "shakshuka-cherry-feta",
                title = "שקשוקת עגבניות שרי שרופות, פלפלים קלויים, פטה כבשים וזעתר טרי",
                shortTitle = "שקשוקת שרי וגבינת פטה",
                description = "עגבניות שרי מתוקות נצרבות במחבת ברזל עד חריכה קלה, מתבשלות עם פלפלים מתוקים, שיני שום ופפריקה מעושנת, עם ביצי חופש רכות ושברי פטה כבשים יוונית.",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCwazI6BHSR4eiFr5kM49Rs6eSFaoDZrawpZKdUy9v9PabHN3GbjGIZ-53E60a8V-sfv35Vm5ojWdPe0OG5mi9ZluFzjWHcOl2GMvRS5Aji612_72PTX4bzr_uYk9IFZvzad5HRTepjbX1c_fz3gt6ZcPBdUwZY0oCjeH_XUwXl7k7FKMFJXCW9YEd5vnZXsOe15su3ROcJ6GyHMfBc-fUDFFGj5NNpHvpv897kj16bUDl9HvjA_wlCSg",
                priceSupermarket = 42.30,
                originalPrice = 58.00,
                savingsBadge = "חיסכון של ₪15.70!",
                remainingBudget = 42.70,
                timeMinutes = 18,
                difficulty = "רמת שף: קל",
                servings = 2,
                category = RecipeCategory.BEST_VALUE,
                dietaryBadge = "צמחוני",
                isChefRecommended = false,
                supermarketIngredientsCount = 4,
                pantryStaplesCount = 2,
                chefTip = "אל תמהרו עם השרי – תנו להן להיחרך ולקבל צריבה מתוקה לפני שאתם מועכים אותן במחבת.",
                restaurantPriceEstimate = 120.0,
                ingredients = listOf(
                    RecipeIngredient("ing-51", "עגבניות שרי תמר מתוקות", "500 גרם", "פירות וירקות", 10.90, false, "שופרסל Fresh"),
                    RecipeIngredient("ing-52", "ביצי חופש טריות L", "מארז 6 יח'", "ביצים", 12.90, false, "שופרסל"),
                    RecipeIngredient("ing-53", "גבינת פטה כבשים אותנטית", "150 גרם", "גבינות ומעדנייה", 13.90, false, "גד"),
                    RecipeIngredient("ing-54", "חלה קלועה או לחם מחמצת", "1 יח'", "לחמים ומאפים", 4.60, false, "מאפיית הבית"),
                    RecipeIngredient("ing-55", "שמן זית, שום, פפריקה מעושנת וזעתר", "לפי הטעם", "תבלינים ומזווה", 0.0, true)
                ),
                steps = listOf(
                    CookingStep(1, "חריכת עגבניות השרי", "במחבת רחבה מחממים שמן זית. מניחים את עגבניות השרי השלמות וצורבים על חום גבוה כ-4 דקות עד שהקליפה נחרכת ומתבקעת.", "חריכת הקליפה מרכזת את המתיקות הטבעית של השרי.", 240, "אש גבוהה"),
                    CookingStep(2, "יצירת רוטב השקשוקה", "מועכים קלות את העגבניות במחבת עם כף עץ, מוסיפים שום פרוס, פפריקה מעושנת וזעתר טרי. מבשלים 5 דקות עד שנוצר רוטב סמיך ועשיר.", timerDurationSeconds = 300, flameLevel = "אש בינונית"),
                    CookingStep(3, "שבירת הביצים", "יוצרים 3-4 גומות ברוטב המבעבע ושוברים לתוכן את ביצי החופש. מכסים ומבשלים 3-4 דקות עד שהחלבון יציב אך החלמון נוזלי וקרמי.", "שמרו על החלמון נוזלי ורוטט.", 210, "אש נמוכה"),
                    CookingStep(4, "תוספת פטה והגשה", "מפוררים מעל גבינת פטה כבשים, מפזרים עלי זעתר רעננים ומגישים מיד במחבת הרותחת לצד חלה טרייה.")
                )
            ),
            ChefRecipe(
                id = "truffle-wild-risotto",
                title = "ריזוטו פטריות וכמהין איטלקי עם כרישה מקורמלת ופרמז׳ן",
                shortTitle = "ריזוטו כמהין ופטריות",
                description = "אורז ארבוריו מובחר מתבשל בסבלנות עם ציר ירקות ארומטי, כרישה מתקתקה, פטריות צרובות וחמאת כמהין עשירה, מועשר בשפע פרמז'ן מגורר.",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDnJdyuJ_eSRj-dQXKolexTgyXkP0GDpElOsME_u8M0zGIr0iL06PtT9Ww8oVD0cA8n2Mu40dh9DGIUo_o_m6ZHJ31msGKpxvbhXXrf8Jfby32vat9Bt5utdX41Z0pBvZiod9ZvP6p4kgjpXrGPwdVeAk_sIutEPntrYOJK0VoNg4PZ3ZiUfVNT4SJa3rT1QcsE5DD2q6RYAy0zba8ND4NoRCVbdHx65mqW2Sm84EwM0j4dmO5ZLwGiZg",
                priceSupermarket = 62.80,
                originalPrice = 95.00,
                savingsBadge = "חיסכון של ₪32.20!",
                remainingBudget = 22.20,
                timeMinutes = 32,
                difficulty = "שף",
                servings = 2,
                category = RecipeCategory.GOURMET,
                dietaryBadge = "צמחוני עילית",
                isChefRecommended = false,
                supermarketIngredientsCount = 4,
                pantryStaplesCount = 3,
                chefTip = "המנטקטורה (Mantecatura) - השלב שבו מכבים את האש וטורפים פנימה חמאה קרה ופרמז'ן - זה מה שהופך אורז לריזוטו חלומי.",
                restaurantPriceEstimate = 190.0,
                ingredients = listOf(
                    RecipeIngredient("ing-61", "אורז ריזוטו ארבוריו איטלקי", "500 גרם", "יבשים ופסטה", 16.90, false, "ריסו גאלו"),
                    RecipeIngredient("ing-62", "סלסלת פטריות מגוונות וכרישה", "350 גרם", "פירות וירקות", 15.20, false, "שופרסל Fresh"),
                    RecipeIngredient("ing-63", "מחית כמהין שחורה איטלקית", "צנצנת קטנה", "שימורים ומעדנייה", 18.90, false, "שופרסל פרימיום"),
                    RecipeIngredient("ing-64", "גבינת פרמז'ן מגוררת", "100 גרם", "גבינות ומעדנייה", 11.80, false, "גד"),
                    RecipeIngredient("ing-65", "חמאה, שמן זית ומלח", "לפי הטעם", "תבלינים ומזווה", 0.0, true)
                ),
                steps = listOf(
                    CookingStep(1, "אידוי כרישה וקיליית האורז", "בסיר רחב מאדים כרישה קצוצה בשמן זית וחמאה. מוסיפים את האורז וקולים כ-2 דקות עד שכל גרגר מצופה ומבריק.", "קיליית האורז נועלת את העמילן בליבת הגרגר.", 120),
                    CookingStep(2, "הזנת ציר בהדרגה", "מוסיפים מצקת מים רותחים/ציר ומערבבים ללא הפסקה עד שהנוזל נספג. ממשיכים מצקת אחר מצקת כ-18 דקות.", "הערבוב המתמיד משחרר עמילן ומייצר קרמיות טבעית.", 1080),
                    CookingStep(3, "צריבת פטריות", "במחבת מקבילה צורבים פטריות בשמן זית עד השחמה עמוקה ומלח ים.", "פטריות צרובות בנפרד שומרות על מרקם פריך ולא ספוג.", 240),
                    CookingStep(4, "מנטקטורה: חמאה, כמהין ופרמז'ן", "מכבים את האש. מוסיפים כפית מחית כמהין, חמאה קרה ושפע פרמז'ן. מערבבים בעוצמה במשך דקה ומכסים ל-2 דקות מנוחה לפני צלחות.", "המרקם חייב להיות 'All'onda' - כמו גל נשפך.", 180)
                )
            )
        )
    }
}
