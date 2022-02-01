package TRDZ.tasks;

import lombok.Value;

@Value
public class Item implements Comparable<Item>
	{
	String name;
	int cost;
	double weight;

	Item(int cost, double weight)
		{
		this.cost=cost;
		this.weight=weight;
		if (cost>99999) name="Драгоценность";
		else if (cost>50000) name="Важность";
		else if (cost>20000) name="Полезность";
		else if (cost>7000) name="База";
		else name="Хлам";
		}

	public double get_Value() {return cost/weight;}

	public int getWeight() {
		if (Initialization.fractional) return (int)(weight*100);
		else return (int)weight;
		}

	@Override
	public String toString() {
		if (weight>Initialization.limit) return String.format ("\n%s %13s ценою в %6d монет с весом %.2f%s",Text_color.RED,name,cost,weight,Text_color.RESET);
		else return String.format ("\n %13s ценою в %6d монет с весом %.2f",name,cost,weight);}

	@Override
	public int compareTo(Item over) { //Метод сравнения двух элементов
		int result = Integer.compare(cost, over.cost);
		if (result==0) return Double.compare(weight, over.weight);
		else return result;
		}
	}
