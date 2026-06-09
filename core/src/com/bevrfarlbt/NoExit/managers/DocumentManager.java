package com.bevrfarlbt.NoExit.managers;

import com.badlogic.gdx.utils.Array;
import com.bevrfarlbt.NoExit.data.Document;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class DocumentManager {
    private static final Preferences prefs = Gdx.app.getPreferences("Documents");

    private static final Array<Document> chapter1 = new Array<>();
    private static final Array<Document> chapter2 = new Array<>();
    private static final Array<Document> chapter3 = new Array<>();
    private static final Array<Document> chapter4 = new Array<>();
    private static final Array<Document> chapter5 = new Array<>();

    public static boolean isCollected(int id) {
        return prefs.getBoolean("doc_" + id, false);
    }

    public static void markAsCollected(Document doc) {

        prefs.putBoolean("doc_" + doc.id, true);
        prefs.flush();
    }

    public static int getCollectedCount(int chapter) {
        init();
        int count = 0;
        for (Document doc : getChapter(chapter)) {
            if (isCollected(doc.id))
                count++;
        }
        return count;
    }

    public static int getTotalCount(int chapter) {
        init();
        return getChapter(chapter).size;
    }

    public static void init() {
        if (chapter1.size > 0) return;
        chapter1.add(new Document(
                1,
                1,
                "Записка #1",
                "Сегодня снова отключали свет в секторе D. Начальство говорит, что это проблемы с проводкой, но ремонтников туда не пускают."
        ));

        chapter1.add(new Document(
                2,
                1,
                "Записка #2",
                "Ночью слышал стук из вентиляции. Слишком громкий для крыс. Слишком ритмичный для механизмов."
        ));

        chapter1.add(new Document(
                3,
                1,
                "Записка #3",
                "После смены пропал Сергей из литейного цеха. В журнале посещений указано, что он покинул территорию. Странно. Его машина всё ещё на парковке."
        ));

        chapter1.add(new Document(
                4,
                1,
                "Записка #4",
                "Нам выдали новые пропуска. Теперь часть помещений отмечена красным цветом. Проход запрещён без объяснения причин."
        ));

        chapter1.add(new Document(
                5,
                1,
                "Записка #5",
                "В столовой ходят слухи о какой-то лаборатории под фабрикой. Большинство смеётся, но никто не может объяснить постоянный шум снизу."
        ));

        chapter1.add(new Document(
                6,
                1,
                "Записка #6",
                "Охрана стала вести себя странно. Слишком много патрулей для обычного производства."
        ));

        chapter1.add(new Document(
                7,
                1,
                "Записка #7",
                "Кто-то оставил на стене надпись: «Не ходите в сектор D». Утром её уже закрасили."
        ));

        chapter1.add(new Document(
                8,
                1,
                "Записка #8",
                "Последние несколько дней мне кажется, что за мной наблюдают камеры, которых раньше не было."
        ));

        chapter2.add(new Document(
                9,
                2,
                "Записка #9",
                "Служебное уведомление: любые разговоры о пропавших сотрудниках считаются распространением ложной информации."
        ));

        chapter2.add(new Document(
                10,
                2,
                "Записка #10",
                "Мы нашли следы крови возле грузового лифта. Руководство приказало немедленно всё убрать."
        ));

        chapter2.add(new Document(
                11,
                2,
                "Записка #11",
                "Вчера охрана оцепила целый этаж. Официальная причина — утечка химикатов. Запаха никто не чувствовал."
        ));

        chapter2.add(new Document(
                12,
                2,
                "Записка #12",
                "Начальник смены сказал, что в случае тревоги мы должны ждать указаний на рабочих местах. Почему нельзя просто эвакуироваться?"
        ));

        chapter2.add(new Document(
                13,
                2,
                "Записка #13",
                "Камеры наблюдения в подземных секторах отключены уже неделю. Техники утверждают, что всё исправно."
        ));

        chapter2.add(new Document(
                14,
                2,
                "Записка #14",
                "В отчёте написано «двое пострадавших». Я лично видел минимум десять носилок."
        ));

        chapter2.add(new Document(
                15,
                2,
                "Записка #15",
                "Сегодня видел, как вооружённая охрана сопровождала людей в белых халатах. Это точно не обычная инспекция."
        ));

        chapter2.add(new Document(
                16,
                2,
                "Записка #16",
                "Кто-то удаляет записи из внутренних журналов. Некоторые даты просто исчезли."
        ));

        chapter3.add(new Document(
                17,
                3,
                "Записка #17",
                "Образец №17 демонстрирует необычную устойчивость к повреждениям тканей."
        ));

        chapter3.add(new Document(
                18,
                3,
                "Записка #18",
                "Экспериментальный материал был доставлен из шахтного комплекса за пределами города."
        ));

        chapter3.add(new Document(
                19,
                3,
                "Записка #19",
                "Испытуемый сохранил двигательную активность после клинической смерти."
        ));

        chapter3.add(new Document(
                20,
                3,
                "Записка #20",
                "Руководство требует ускорить исследования независимо от рисков."
        ));

        chapter3.add(new Document(
                21,
                3,
                "Записка #21",
                "Во время тестирования объект проявил агрессию к персоналу."
        ));

        chapter3.add(new Document(
                22,
                3,
                "Записка #22",
                "Мы больше не уверены, что имеем дело с заболеванием. Возможно, это совершенно новая форма организма."
        ));

        chapter3.add(new Document(
                23,
                3,
                "Записка #23",
                "Все данные по проекту переведены под высший уровень секретности."
        ));

        chapter3.add(new Document(
                24,
                3,
                "Записка #24",
                "Если кто-то найдёт эти записи — остановите эксперимент. Мы уже потеряли контроль."
        ));

        chapter4.add(new Document(
                25,
                4,
                "Записка #25",
                "Экстренное сообщение: немедленно покиньте лабораторный сектор."
        ));

        chapter4.add(new Document(
                26,
                4,
                "Записка #26",
                "Они ломятся в двери уже несколько часов. Боеприпасы заканчиваются."
        ));

        chapter4.add(new Document(
                27,
                4,
                "Записка #27",
                "Ворота фабрики закрыты автоматически. Мы не можем выбраться наружу."
        ));

        chapter4.add(new Document(
                28,
                4,
                "Записка #28",
                "Некоторые заражённые продолжают узнавать коллег. Это самое страшное."
        ));

        chapter4.add(new Document(
                29,
                4,
                "Записка #29",
                "Охрана получила приказ стрелять без предупреждения."
        ));

        chapter4.add(new Document(
                30,
                4,
                "Записка #30",
                "Связь с городом потеряна. Неизвестно, знает ли кто-нибудь о происходящем."
        ));

        chapter4.add(new Document(
                31,
                4,
                "Записка #31",
                "Мы пытались уничтожить образец №17. Это не помогло."
        ));

        chapter4.add(new Document(
                32,
                4,
                "Записка #32",
                "Если вы читаете это, значит система изоляции провалилась."
        ));

        chapter5.add(new Document(
                33,
                5,
                "Записка #33",
                "Проект был создан не для лечения болезней. Это была лишь официальная легенда."
        ));

        chapter5.add(new Document(
                34,
                5,
                "Записка #34",
                "Руководство знало о рисках ещё до начала испытаний."
        ));

        chapter5.add(new Document(
                35,
                5,
                "Записка #35",
                "Все сотрудники фабрики были признаны расходным материалом."
        ));

        chapter5.add(new Document(
                36,
                5,
                "Записка #36",
                "Образец №17 обнаружили много лет назад во время геологических работ."
        ));

        chapter5.add(new Document(
                37,
                5,
                "Записка #37",
                "Исследователи считали, что объект обладает способностью изменять живые ткани."
        ));

        chapter5.add(new Document(
                38,
                5,
                "Записка #38",
                "После первых смертей проект должны были закрыть. Вместо этого финансирование увеличили."
        ));

        chapter5.add(new Document(
                39,
                5,
                "Записка #39",
                "Изоляция фабрики была запланирована заранее. План существовал ещё до катастрофы."
        ));

        chapter5.add(new Document(
                40,
                5,
                "Записка #40",
                "Финальная запись директора проекта: Если комплекс всё ещё функционирует, значит объект выжил. Если кто-то читает этот документ, значит эксперимент продолжается."
        ));
    }

    public static Document getEpilogue() {
        init();
        return new Document(
                41,
                99,
                "ЭПИЛОГ",
                "Журнал восстановления системы.\n\n" +
                        "Обнаружен активный субъект.\n\n" +
                        "Попытка побега №1 — неудачна.\n" +
                        "Попытка побега №12 — неудачна.\n" +
                        "Попытка побега №47 — неудачна.\n" +
                        "...\n\n" +
                        "Попытка побега №??? — выполняется.\n\n" +
                        "Добро пожаловать обратно, сотрудник."
        );
    }

    private static Array<Document> getChapter(int chapter) {
        switch (chapter) {
            case 1: return chapter1;
            case 2: return chapter2;
            case 3: return chapter3;
            case 4: return chapter4;
            case 5: return chapter5;
            default: return chapter1;
        }
    }

    public static int getCurrentChapter() {

        if (!isChapterCompleted(1))
            return 1;

        if (!isChapterCompleted(2))
            return 2;

        if (!isChapterCompleted(3))
            return 3;

        if (!isChapterCompleted(4))
            return 4;

        if (!isChapterCompleted(5))
            return 5;

        return 99;
    }

    public static boolean allDocumentsCollected() {
        for (int i = 1; i <= 40; i++) {
            if (!isCollected(i))
                return false;
        }
        return true;
    }

    public static boolean isChapterCompleted(int chapter) {
        for (Document doc : getChapter(chapter)) {
            if (!isCollected(doc.id))
                return false;
        }
        return true;
    }

    public static Document getRandomDocument(int chapter) {
        init();
        Array<Document> available = new Array<>();
        for (Document doc : getChapter(chapter)) {
            if (!isCollected(doc.id)) {
                available.add(doc);
            }
        }
        if (available.size == 0)
            return null;
        return available.random();
    }

    public static Array<Document> getCollectedDocuments() {
        init();
        Array<Document> result = new Array<>();

        for (Document doc : chapter1)
            if (isCollected(doc.id))
                result.add(doc);

        for (Document doc : chapter2)
            if (isCollected(doc.id))
                result.add(doc);

        for (Document doc : chapter3)
            if (isCollected(doc.id))
                result.add(doc);

        for (Document doc : chapter4)
            if (isCollected(doc.id))
                result.add(doc);

        for (Document doc : chapter5)
            if (isCollected(doc.id))
                result.add(doc);

        if (isCollected(41)) {
            result.add(getEpilogue());
        }

        return result;
    }
}