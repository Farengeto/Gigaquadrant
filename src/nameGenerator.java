import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class nameGenerator {
	public double[][] frequency = new double[27][27];
	public double[][] totalf = new double[27][27];
	public double[] initial = new double[27];
	public double[] totali = new double[27];
	
	public nameGenerator(File file){
		//initialization
		int[] letter = new int[27];
		int words = 0;
		int total = 0;
		for(int i = 0; i < 27; i++){
			letter[i] = 0;
			initial[i] = 0;
			for(int j = 0; j < 27; j++){
				frequency[i][j] = 0;
				totalf[i][j] = 0;
			}
		}
		
		//letter pair input
		try{
			Scanner sc = new Scanner(file);
			while(sc.hasNext()){
				String word = sc.next();
				//System.out.println(word);
				word = word.toLowerCase();
				int i;
				for(i = 0; i < word.length() && (word.charAt(i) < 96 || word.charAt(i) > 123); i++){
				}
				if(i < word.length() && word.charAt(i) > 96 && word.charAt(i) < 123){
					initial[word.charAt(i)-'a']++;
					words++;
					//System.out.println((word.charAt(i)-'a'));
				}
				for(i = i; i < word.length()-1; i++){
					if(word.charAt(i) > 96 && word.charAt(i) < 123){
						letter[word.charAt(i)-'a']++;
						if(word.charAt(i+1) > 96 && word.charAt(i+1) < 123){
							frequency[word.charAt(i)-'a'][word.charAt(i+1)-'a']++;
							//System.out.println(word.substring(i, i+2) + " " + (word.charAt(i)-'a') + " " + (word.charAt(i+1)-'a'));
						}
						else{
							frequency[word.charAt(i)-'a'][26]++;
						}
						total++;
					}
				}
				if(word.charAt(word.length()-1) > 96 && word.charAt(word.length()-1) < 123){
					letter[word.charAt(word.length()-1)-'a']++;
					frequency[word.charAt(word.length()-1)-'a'][26]++;
					total++;
				}
			}
			sc.close();
		}catch(IOException ex){}
		
		//sample table test output
		/*for(int i = 0; i < 26; i++){
			System.out.print((int)initial[i] + " -");
			for(int j = 0; j < 26; j++){
				System.out.print(" " + (int)frequency[i][j]);
			}
			System.out.println(" - " + (int)frequency[i][26]);
		}*/
		
		//letter pair frequency calculation
		for(int i = 0; i < 26; i++){
			initial[i] = initial[i] / (double)words;
			if(i == 0){
				totali[0] = initial[0];
			}
			else{
				totali[i] = totali[i-1] + initial[i];
			}
			for(int j = 0; j < 27; j++){
				frequency[i][j] = frequency[i][j] / (double)letter[i];
				if(j == 0){
					totalf[i][0] = frequency[i][0];
				}
				else{
					totalf[i][j] = totalf[i][j-1] + frequency[i][j];
				}
			}
		}
		
		//sample table test output
		/*for(int i = 0; i < 26; i++){
			System.out.print(initial[i] + " -");
			for(int j = 0; j < 26; j++){
				System.out.print(" " + frequency[i][j]);
			}
			System.out.println(" - " + frequency[i][26]);
		}
		for(int i = 0; i < 26; i++){
			System.out.print((char)(i+97) + " " + totali[i] + " -");
			for(int j = 0; j < 26; j++){
				System.out.print(" " +(char)(j+97)+ " " + totalf[i][j]);
			}
			System.out.println(" - " + totalf[i][26]);
		}*/
		
		//correcting for zero
		/*for(int i = 0; i < 26; i++){
			for(int j = 25; j > 0; j--){
				if(totalf[i][j] == totalf[i][j-1]){
					totalf[i][j] = totalf[i][j+1];
				}
			}
		}*/
		
		//System.out.println("generator ready");
	}
	
	public String generate(){
		String word = "";
		double n = Math.random();
		int last = 26;
		int i = 0;
		boolean check = false;
		for(i = 0; !check && i < 26; i++){
			if(n < totali[i]){
				check = true;
				//System.out.println(last + " " + i + " " + frequency[last][i]);
				word = word + (char)(i+65);
				//System.out.print((char)(i+65));
				last = i;
			}
		}
		//System.out.print(last + " ");
		int count = 1;
		while(last < 26 && count < 100){
			n = Math.random();
			check = false;
			for(i = 0; !check && i < 27; i++){
				if(n < totalf[last][i]){
					check = true;
					//System.out.println(last + " " + i + " " + frequency[last][i]);
					if(frequency[last][i] == 0){
						System.out.println("Error:");
						if(i>0){
							System.out.println(totalf[last][i-1]);
						}
						System.out.println(n);
						System.out.println(totalf[last][i]);
					}
					if(i < 26){
						word = word + (char)(i+97);
						//System.out.print((char)(i+97));
					}
				}
			}
			//if(i < 26 && (i >= 26 && count < 5)){
			last = i-1;
			//}
			//System.out.print(last + " ");
			count++;
		}
		/*int count = 1;
		while(last < 26){
			n = Math.random();
			check = false;
			for(i = 0; !check && i < 27; i++){
				//System.out.println(i);
				if(n < totalf[last][i]*Math.pow(0.95,count) && i < 26){
					check = true;
					word = word + (char)(i+97);
					System.out.print((char)(i+97));
					last = i;
					count++;
				}
			}
			System.out.println(Math.pow(0.95,count));
		}
		*/
		/*if(n < totali[i]){
			word = word + (char)(i+65);
			l = i;
			System.out.println(totali[i+1]);
			System.out.print((char)(i+65));
		}
		System.out.println("<first letter>");
		//int count = 1;
		do{
			n = Math.random();
			System.out.println(n);
			for(i = 0; n > totalf[l][i] && i < 27; i++){
				//System.out.println(totalf[l][i]);
			}
			if(n < totali[i] && i != 26){
				word = word + (char)(i+97);
				System.out.println(totalf[l][i]);
				System.out.print((char)(i+97));
			}
			l = i;
		}while(l != 26 && i != 26);
		System.out.println("Generated");
		*/
		return word;
	}
}
