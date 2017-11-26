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
        /*  15 alacak olan arrayi rasgele seçmedim.	p1 arrayi 15 taş alıyor.
         *  Eller dağıtılacağı zaman oyuncu rasgele seçilebilir ve p1 arrayi ona verilir. Yani 1. oyuncu olmak zorunda değil.
         */
        int[] p1 = Arrays.copyOfRange(taslar, 0, 15);
        int[] p2 = Arrays.copyOfRange(taslar, 15, 29);
        int[] p3 = Arrays.copyOfRange(taslar, 29, 43);
        int[] p4 = Arrays.copyOfRange(taslar, 43, 57);
        p1 = okeyAyar(p1);
        p2 = okeyAyar(p2);
        p3 = okeyAyar(p3);
        p4 = okeyAyar(p4);
        
        int[] puanlar = new int[4];
        System.out.println("1. oyuncu:\n" + Arrays.toString(p1));
        puanlar[0] = puanHesapla(p1);
        System.out.println("2. oyuncu:\n" + Arrays.toString(p2));
        puanlar[1] = puanHesapla(p2);
        System.out.println("3. oyuncu:\n" + Arrays.toString(p3));
        puanlar[2] = puanHesapla(p3);
        System.out.println("4. oyuncu:\n" + Arrays.toString(p4));
        puanlar[3] = puanHesapla(p4);
        
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
     * https://stackoverflow.com/questions/1683702/finding-if-a-valid-rummikub-solution-exist-from-selected-tiles
     * 
     * @return eldeki kullanılan taş sayısı
     */
    static int puanHesapla(int[] el) {
    	List<List<Integer>> seriler = butunSeriler(el);	//	seriler: aslında hem seriler hem gruplar
    	seriler.addAll(butunGruplar(el));
    	for (List<Integer> list : seriler) {
    		System.out.println("list: " + list);
    	}
    	
    	int max = 0;
    	for (List<Integer> list : seriler) {
    		List<Integer> elList = Arrays.stream(el).boxed().collect(Collectors.toList());
    		elList.removeAll(list);
    		if (elList.isEmpty())
    			return list.size();
    		
    		int puan = puanHesapla(elList.stream().mapToInt(i -> i).toArray());
    		
    		max = Math.max(puan + list.size(), max);;
    	}
    	
    	return max;
    }
    
    static List<List<Integer>> butunGruplar(int[] el) {
    	List<List<Integer>> gruplar = new ArrayList<List<Integer>>();
    	for (int i = 0; i < 13; i++) {
    		gruplar.add(new ArrayList<Integer>());
    	}
    	
		for (int i = 0; i < el.length; i++) {
			if (!gruplar.get(el[i] % 13).contains(el[i]))
				gruplar.get(el[i] % 13).add(el[i]);
    	}
		List<List<Integer>> ret = new ArrayList<List<Integer>>();
		for (List<Integer> sayiGrubu : gruplar) {
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
     * Bütün serileri döner örn. 1 2 3, 6 7 8
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
    			else if (el[j] == el[j - 1]) {
    				continue;
    			}
    			else 
    				break;
    			//System.out.println(dizi);
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
