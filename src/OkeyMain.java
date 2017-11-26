import java.util.*;
import java.util.stream.Collectors;

public class OkeyMain {

	final static int TAS_SAYISI = 106;
	final static int DESTE = 53;	//	TAS_SAYISI / 2
	
	/** puan hesaplanmadan önce diğer taşlarla karışmasın diye okeyler bu sayıya (OK) dönüştürülüyor (bkz. okeyAyar) */
	final static int OK = 99;
	
	static int okey = -1;	//	okey taşı: gösterge bulunduktan sonra programın içinde set edilicek
	
    public static void main(String[] args) {
    	
        List<Integer> taslar_list = new ArrayList<Integer>();
        /*  taşları listeye ekle */
        for (int i = 0; i < TAS_SAYISI; i++) {
            taslar_list.add(i % DESTE);
        }
        Collections.shuffle(taslar_list);   //  taşları karıştır
        int[] taslar = taslar_list.stream().mapToInt(i -> i).toArray();
        int gosterge = gostergeSec(taslar);
        okey = okeyBul(gosterge);
        System.out.println("Okey: " + okey);
        /*  15 alacak olan arrayi rasgele seçmedim.	p1 arrayi 15 taş alıyor.
         *  Eller dağıtılacağı zaman oyuncu rasgele seçilebilir ve p1 arrayi ona verilir. Yani 1. oyuncu olmak zorunda değil.
         */
        int[] p1 = Arrays.copyOfRange(taslar, 0, 15);
        int[] p2 = Arrays.copyOfRange(taslar, 15, 29);
        int[] p3 = Arrays.copyOfRange(taslar, 29, 43);
        int[] p4 = Arrays.copyOfRange(taslar, 43, 57);
        System.out.println("1. oyuncu:\n" + Arrays.toString(p1));
        System.out.println("2. oyuncu:\n" + Arrays.toString(p2));
        System.out.println("3. oyuncu:\n" + Arrays.toString(p3));
        System.out.println("4. oyuncu:\n" + Arrays.toString(p4));
        
        p1 = okeyAyar(p1);
        p2 = okeyAyar(p2);
        p3 = okeyAyar(p3);
        p4 = okeyAyar(p4);
        
        int[] puanlar = new int[4];
        
        puanlar[0] = Math.max(puanHesapla(p1), ciftPuani(p1));
        puanlar[1] = Math.max(puanHesapla(p2), ciftPuani(p2));
        puanlar[2] = Math.max(puanHesapla(p3), ciftPuani(p3));
        puanlar[3] = Math.max(puanHesapla(p4), ciftPuani(p4));
        
        System.out.println("Puanlar:");
        int birinci = -1;
        int max = -1;
        for (int j = 0; j < 4; j++) {
        	if (puanlar[j] > max) {
        		max = puanlar[j];
        		birinci = j;
        	}
        	System.out.println((j + 1) + ". oyuncu: " + puanlar[j] + " puan");
        }
        System.out.println("En iyi el " + (birinci + 1) + ". oyuncuda.");
    }
    
    /**
     * 
     * @param el
     * @return	Çifte giderse elin alacağı puan
     */
    static int ciftPuani(int[] el) {
    	int cift = 0;
    	for (int i = 1; i < el.length; i++) {
    		if (el[i] == el[i - 1])
    			cift++;
    	}
    	if (el[el.length - 1] == OK)
    		cift++;
    	return cift * 2;
    }
    
    /**
     * Okeyleri 99'a (OK) dönüştür. Sahte okeylere de okeyin değerini ata. Eli sort et. 
     * @param el
     * @return
     */
    static int[] okeyAyar(int[] el) {
    	for (int i = 0; i < el.length; i++) {
    		if (el[i] == okey)
    			el[i] = OK;
    	}
    	for (int i = 0; i < el.length; i++) {
    		if (el[i] == 52)
    			el[i] = okey;
    	}
    	Arrays.sort(el);
    	return el;
    }
    
    /**
     * Verilen elin puanını hesaplar. Çok olursa daha iyi
     * 
     * @return eldeki kullanılan taş sayısı
     */
    static int puanHesapla(int[] el) {
    	List<List<Integer>> seriler = butunSeriler(el);	//	seriler: aslında hem seriler hem gruplar
    	seriler.addAll(butunGruplar(el));

    	int max = 0;
    	for (List<Integer> list : seriler) {
    		List<Integer> elList = Arrays.stream(el).boxed().collect(Collectors.toList());
    		elList.removeAll(list);
    		if (elList.isEmpty())
    			return list.size();
    		
    		int puan = puanHesapla(elList.stream().mapToInt(i -> i).toArray());
    		
    		max = Math.max(puan + list.size(), max);
    	}
    	
    	return max;
    }
    
    static List<List<Integer>> butunGruplar(int[] el) {
    	List<List<Integer>> gruplar = new ArrayList<List<Integer>>();
    	for (int i = 0; i < 13; i++) {
    		gruplar.add(new ArrayList<Integer>());
    		if (el[el.length - 1] == OK)
    			gruplar.get(i).add(OK);
    		if (el[el.length - 2] == OK)
    			gruplar.get(i).add(OK);
    	}
    	
		for (int i = 0; i < el.length; i++) {
			if (!gruplar.get(el[i] % 13).contains(el[i]))
				gruplar.get(el[i] % 13).add(el[i]);
    	}
		
		List<List<Integer>> ret = new ArrayList<List<Integer>>();
		for (List<Integer> sayiGrubu : gruplar) {
			while (sayiGrubu.size() > 4) {
				sayiGrubu.remove(Integer.valueOf(OK));
			}
			if (sayiGrubu.size() >= 3)
				ret.add(sayiGrubu);
			if (sayiGrubu.size() == 4) {
				for (int i = 0; i < 4; i++) {
					List<Integer> temp = new ArrayList<Integer>(sayiGrubu);
					temp.remove(i);
					ret.add(temp);
				}
			}
		}
    	return ret;
    }
    
    /**
     * Sadece debug için.
     * 
     * @param el
     * @return
     */
    static String eliYazdir(int[] el) {
    	String elOut = "0 ";
    	int suit = 0;
    	for (int i = 0; i < el.length; i++) {
    		if (suit != el[i] / 13) {
    			suit++;
    			elOut += "\n" + suit + " ";
    		}
    		elOut += (el[i] % 13) + " ";
    	}
    	return elOut;
    }
    
    /**
     * Bütün serileri döner örn. 1 2 3
     * @param el
     * @return
     */
    static List<List<Integer>> butunSeriler(int[] el) {
    	List<List<Integer>> seriler = new ArrayList<List<Integer>>();
    	int renk = 0;
    	for (int i = 0; i < el.length; i++) {
    		List<Integer> dizi = new ArrayList<Integer>();
    		dizi.add(el[i]);
    		renk = el[i] / 13;
    		
    		for (int j = i + 1; j < el.length; j++) {
    			if (el[j] / 13 != renk)
    				break;
    			
    			if (el[j] - 1 == el[j - 1])
    				dizi.add(el[j]);
    			else if (el[j] - 2 == el[j - 1] && el[el.length - 1] == OK) {
    				dizi.add(OK);
    				dizi.add(el[j]);
    			}
    			else if (el[j] == el[j - 1]) {
    				continue;
    			}
    			else 
    				break;
    			if (dizi.size() == 2 && el[el.length - 1] == OK)
    				dizi.add(OK);
    			if (dizi.size() > 2) {
    				List<Integer> copy = new ArrayList<Integer>(dizi);
    				seriler.add(copy);
    			}
    				
    		}
    	}
    	return seriler;
    }
    
    /**
     * Zaten karışmış olduğu için verilen dizinin sonundaki taşı döner. Sahte okey gelirse bir öncekini döner.
     * 
     * @param taslar
     * @return	gösterge
     */
    static int gostergeSec(int[] taslar) {
    	return taslar[TAS_SAYISI - 1] != 52 ? taslar[TAS_SAYISI - 1] : taslar[TAS_SAYISI - 2];
    }
    
    static int okeyBul(int gosterge) {
    	return (gosterge + 1) % 13 == 0 ? gosterge - 12 : gosterge + 1;
    }
}
