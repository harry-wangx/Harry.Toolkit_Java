package harry.toolkit.app;

import java.util.Arrays;
import java.util.Random;

import harry.common.IdCreator;

public class Program {
	static IdCreator creator;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			creator=new IdCreator();
		}catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		
		System.out.println(creator.create());
		System.out.println(creator.create());
		System.out.println(creator.create());
		System.out.println(creator.create());
		System.out.println(creator.create());
		
		System.out.println("---------------------");

		System.out.println(creator.getDefault().create());
		System.out.println(creator.getDefault().create());
		System.out.println(creator.getDefault().create());
		System.out.println(creator.getDefault().create());
		System.out.println(creator.getDefault().create());
	}

}
