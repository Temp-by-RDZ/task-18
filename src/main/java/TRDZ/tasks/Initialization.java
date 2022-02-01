package TRDZ.tasks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

/*  Про табуляции: они равны 4 пробелам и я не знаю почему это так странно выглядят в гите,
	ведь при загрузке с гита у меня все отображается правильно.
	Если же в другом месте табуляции выглядят большими, то это может быть только,
	из-за того что я не отправляю в гит свои настройки idea */
public class Initialization
	{
	public static int limit = 5;				//Лимит вместимости группы
	public static boolean fractional = true; 	//Вес вещей генерировать в дробном формате?
	public static boolean sorting = false;		//В динамическом подходе с выводом сортировать список? (К времени выполнения будет добавлено время сортировки)

	public static void main(String[] args) {
		ArrayList<Item> Items = generate(10);
		System.out.printf("1. Возведенное в степень %.2f число %d = %.2f\n",-0.5,2,step(2,-0.5));
		System.out.println("2. Вычисление заполнения рюкзака.");
		boolean show = true; 		//Выводить ли значения массива?
		boolean show2 = false; 		//Выводить ли состав группы?
		Items_sort(Items);
		if (show) {
			System.out.print("Сгенерированный набор вещей ");
			System.out.println(Items);
			}
		if (show2) {
			long startTime = System.currentTimeMillis();
			din_analyzer(Items, limit);
			long endTime = System.currentTimeMillis();
			System.out.println("Затраченное время: " + (endTime - startTime));
			}
		else  {
			long startTime = System.currentTimeMillis();
			din_finder(Items,limit);
			long endTime = System.currentTimeMillis();
			System.out.println("Затраченное время: " + (endTime - startTime));
			}
		System.out.printf("Поиск значения наилучшей группы среди %d элементов при лимите в %d способом перебора рекурсией.\n",Items.size(),limit);
		long startTime = System.currentTimeMillis();
		if (fractional) System.out.println("  Сумма наилучшей группы - "+classic(Items,Items.size(),limit*100));
		else System.out.println("  Сумма наилучшей группы - "+classic(Items,Items.size(),limit*100));
		long endTime = System.currentTimeMillis();
		System.out.println("Затраченное время: " + (endTime - startTime));
		}

	/**
	 * Вычисление суммы наилучшей группы путем перебора
	 * @param Items Список элементов
	 * @param capacity Размер для формирования группы
	 */
	public static int classic(ArrayList<Item> Items, int total, int capacity) {
		if (total<=0) return 0;
		else if (Items.get(total-1).getWeight()>capacity) return classic(Items,total-1,capacity);
		else return Math.max(classic(Items,total-1,capacity), Items.get(total-1).getCost()+classic(Items,total-1,capacity-Items.get(total-1).getWeight()));
		}

	/**
	 * Вычисление суммы наилучшей группы с подходом динамического программирования
	 * @param Items Список элементов, во избежание оптимизации
	 * способной повлиять на замеры дублируется.
	 * @param capacity Размер для формирования группы
	 */
	public static void din_finder(ArrayList<Item> Items,int capacity) {
		System.out.printf("Поиск значения наилучшей группы среди %d элементов при лимите в %d способом динамического программирования.\n",Items.size(),capacity);
		if (fractional) capacity*=100;
	//region Создаем упраздненный список
		ArrayList<Item> Items_obr = new ArrayList<>();
		for (int i = Items.size()-1; i >= 0 ; i--) {
			if (Items.get(i).getWeight()<=capacity) Items_obr.add(Items.get(i));
			}
	//region Вычисляем таблицу для первого элемента
	//endregion
		int[][] Dynamic_map = new int[Items_obr.size()][capacity+1];
		for (int j = 1; j < Dynamic_map[0].length; j++) {//Вес
			if (Items_obr.get(0).getWeight()<=j) Dynamic_map[0][j]=Items_obr.get(0).getCost();
			}
	//endregion
	//region Вычисляем всю таблицу заполнения
		for (int i=1; i<Dynamic_map.length; i++) {//Вещь
			for (int j = 1; j < Dynamic_map[0].length; j++) {//Вес
				if (Items_obr.get(i).getWeight()<=j) {
					Dynamic_map[i][j]=Math.max(Dynamic_map[i-1][j],Items_obr.get(i).getCost()+Dynamic_map[i-1][Math.max(0,j-Items_obr.get(i).getWeight())]);}
				else Dynamic_map[i][j]=Dynamic_map[i-1][j];
				}
			}
	//endregion
		System.out.println("  Сумма наилучшей группы - "+Dynamic_map[Dynamic_map.length-1][Dynamic_map[0].length-1]);
		}

	/**
	 * Вычисление наилучшей группы с подходом динамического программирования
	 * @param Items Список элементов, во избежание оптимизации
	 * способной повлиять на замеры дублируется.
	 * @param capacity Размер для формирования группы
	 */
	public static void din_analyzer(ArrayList<Item> Items, int capacity) {
		/* Данный тип сборки группы был сделан для наглядности.*/
		System.out.printf("Поиск наилучшей группы среди %d элементов при лимите в %d способом динамического программирования.\n",Items.size(),capacity);
		if (fractional) capacity*=100;
	//region Создаем упраздненный список
		ArrayList<Item> Items_obr = new ArrayList<>();
		for (int i = Items.size()-1; i >= 0 ; i--) {
			if (Items.get(i).getWeight()<=capacity) Items_obr.add(Items.get(i));
			}
	//endregion
		if (sorting) Items_sort_byValue(Items_obr);
		int[][] Dynamic_map = new int[Items_obr.size()][capacity+1];
		if (Items_obr.size()==0) {System.out.println("  Все вещи были слишком большими и тяжелыми для текущего лимита"); return;}
		HashSet<Item> tek = new HashSet<>();
		ArrayList<HashSet<Item>> prv = new ArrayList<>();
		ArrayList<HashSet<Item>> nxt = new ArrayList<>();
		prv.add(new HashSet<>());
		nxt.add(new HashSet<>());
	//region Вычисляем таблицу для первого элемента
		for (int j = 1; j < Dynamic_map[0].length; j++) {//Вес
			tek= new HashSet<>();
			if (Items_obr.get(0).getWeight()<=j) {
				tek.add(Items_obr.get(0));
				Dynamic_map[0][j]=Items_obr.get(0).getCost();
				}
			prv.add(tek);
			}
	//endregion
	//region Вычисляем всю таблицу заполнения
		for (int i=1; i<Dynamic_map.length; i++) {//Вещь
			for (int j = 1; j < Dynamic_map[0].length; j++) {//Вес
				tek= new HashSet<>();
				if (Items_obr.get(i).getWeight()<=j) {
					if ((Dynamic_map[i-1][j])<(Items_obr.get(i).getCost()+Dynamic_map[i-1][Math.max(0,j-Items_obr.get(i).getWeight())])) {
						tek.add(Items_obr.get(i));
						tek.addAll(prv.get(j-Items_obr.get(i).getWeight()));
						Dynamic_map[i][j]=Items_obr.get(i).getCost()+Dynamic_map[i-1][Math.max(0,j-Items_obr.get(i).getWeight())];}
					else {
						tek.addAll(prv.get(j));
						Dynamic_map[i][j]=Dynamic_map[i-1][j];
						}
					}
				else {
					Dynamic_map[i][j]=Dynamic_map[i-1][j];
					tek.addAll(prv.get(j));
					}
				nxt.add(tek);
				}
			prv = new ArrayList<>(nxt);
			nxt= new ArrayList<>();
			nxt.add(new HashSet<>());
			}
	//endregion
		System.out.println("  Наилучшая группа: - "+tek);
		System.out.println("  Сумма наилучшей группы - "+Dynamic_map[Dynamic_map.length-1][Dynamic_map[0].length-1]);
		}

	/**
	 * Генерация набора вещей
	 * @param total сколько всего вещей
	 * @return массив вещей
	 */
	public static ArrayList<Item> generate(int total) {
		ArrayList<Item> result = new ArrayList<>();
		for (int i = 0; i < total; i++) {
			if (fractional) result.add(new Item((1+(int)(Math.random()*219))*500,0.1+(Math.random()*9.9)));
			else result.add(new Item((1+(int)(Math.random()*219))*500,(double)1+(int)(Math.random()*9)));
			}
		return result;
		}

	/**
	 * Классическая сортировка вещей
	 * @param Items массив вещей
	 */
	public static void Items_sort(ArrayList<Item> Items) {
		Comparator<Item> comp = new Comparator<Item>() {
			@Override
			public int compare(Item o1, Item o2) {return o1.compareTo(o2);}
			};
		Items.sort(comp.reversed());
		}
	/**
	 * Сортировка вещей по их соотношению
	 * @param Items массив вещей
	 */
	public static void Items_sort_byValue(ArrayList<Item> Items) {
		Comparator<Item> comp = Comparator.comparing(Item::get_Value);
		Items.sort(comp.reversed());
		}

	/**
	 * Логическая обработка вычисления целочисленной степени рекурсией
	 * @param num число
	 * @param step степень
	 * @return результат
	 */
	public static double step(int num, int step) {
		if (num==0||num==1) return num;
		if (step>0) return R_step(num,step);
		else return 1/(double)L_step(num,step);
		}
	/**
	 * Логическая обработка вычисления дробной степени рекурсией
	 * @param num число
	 * @param step степень
	 * @return результат
	 */
	public static double step(int num, double step) {
		if (num<1) return num;
		step*=10;
		if (step>0) return Math.pow(R_step(num,(int)step), 1.0/10);
		else return 1/Math.pow(L_step(num,(int)step), 1.0/10);
		}

	/**
	 * Вычисление степени рекурсией в положительно случае
	 * @param num число
	 * @param step степень
	 * @return результат
	 */
	public static int R_step(int num, int step) {
		if (step==0) return 1;
		return num*R_step(num,step-1);
		}
	/**
	 * Вычисление степени рекурсией в отрицательном случае
	 * @param num число
	 * @param step степень
	 * @return результат
	 */
	public static int L_step(int num, int step) {
		if (step==0) return 1;
		return num*L_step(num,step+1);
		}
	}
