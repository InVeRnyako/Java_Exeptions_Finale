1. Запрос строки
2. Проверка на возможность разделения и разделение строки на необходимые составляющие
3. Проверка валидности состовляющих
	3.1. ФИО:
		От 1 до 35 символов (1 для '-' для ФИО без отчества), без специальных символов кроме '-'
	3.2. Дата рождения:
		Правильность формата, реалистичность даты (от 100 лет назад до сегодня)
	3.3. Номер телефона:
		Беззнаковость (по заданию) 11 символов, международный формат.
	3.4. Пол:
		f или m, проверка на 2 возможных варианта.
4. Сохранение данных в случае отсутствия ошибок.
	4.1. Реформатирование данных под ТЗ, подготовка строки
	4.2. Выделение фамилии в название файла, проверка на наличие файла
	4.3. Создание файла в случае отсутствия
	4.4. Добавление новой строки к существующему файлу.
5. Обработка ошибок
	5.1. Ошибки при вводе данных выдают краткое описание ошибки
	5.2. Ошибки при работе с файлами выдают краткое описание и StackTrace