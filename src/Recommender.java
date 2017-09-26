package MovieRec ;

import java.io.* ;
import java.util.* ;
import com.google.common.collect.* ;
import org.apache.commons.configuration.* ;

public class 
Recommender
{
	TreeMap<Integer, Integer> 
	support1 = new TreeMap<Integer, Integer>() ; 
	/* support1 : MovieId -> Num */

	TreeMap<IntPair, Integer> 
	support2 = new TreeMap<IntPair, Integer>() ; 
	/* support2 : MovieId x MovieId -> Num */

	TreeMap<IntTriple, Integer> 
	support3 = new TreeMap<IntTriple, Integer>() ; 
	/* support3 : MovieId x MovieId x MovieId -> Num */

	PropertiesConfiguration config ;
	int min_supports ;
	int min_evidence_3 ;
	double threshold_2 ;
	double threshold_3 ;


	Recommender(PropertiesConfiguration config) {
		this.config = config ;
		this.min_supports = 
			config.getInt("training.min_supports") ;
		this.threshold_2 = 
			config.getDouble("prediction.threshold_2") ;
		this.threshold_3 = 
			config.getDouble("prediction.threshold_3") ;
		this.min_evidence_3 = 
			config.getInt("prediction.min_evidence_3") ;
	}

	public 
	void train(MovieRatingData data) {
		TreeMap<Integer, HashSet<Integer>> 
		Baskets = data.getBaskets() ;
		/* Baskets : UserID -> Set<MovieId> */

		for (Integer user : Baskets.keySet()) {
			HashSet<Integer> Basket = Baskets.get(user) ;

			updateSupport1(Basket) ;
			updateSupport2(Basket) ;
			updateSupport3(Basket) ;
		}
	}

	public
	int predict(HashSet<Integer> profile, Integer q) {
		if (predictPair(profile, q) == 1)
			return 1 ;
		return predictTriple(profile, q) ;
	}


	private
	void updateSupport1(HashSet<Integer> Basket) {
		for (Integer item : Basket) {
			Integer c = support1.get(item) ;
			if (c == null)
				c = new Integer(1) ;
			else
				c = new Integer(c.intValue() + 1 ) ;
			support1.put(item, c) ;
		}
	}

	private
	void updateSupport2(HashSet<Integer> Basket) {
		if (Basket.size() >= 2) {
			for (Set<Integer> pair : Sets.combinations(Basket, 2)) {
				Integer c = support2.get(new IntPair(pair)) ;
				if (c == null) 
					c = new Integer(1) ;
				else
					c = new Integer(c.intValue() + 1) ;
				support2.put(new IntPair(pair), c) ;
			}
		}
	}

	private
	void updateSupport3(HashSet<Integer> Basket) {
		HashSet<Integer> 
		_Basket = new HashSet<Integer>() ;
		for (Integer elem : Basket) {
			if (support1.get(elem) >= min_supports)
				_Basket.add(elem) ;
		}
		Basket = _Basket ;

		if (Basket.size() >= 3) {
			for (Set<Integer> triple : Sets.combinations(Basket, 3)) {
				Integer c = support3.get(new IntTriple(triple));
				if (c == null) 
					c = new Integer(1) ;
				else
					c = new Integer(c.intValue() + 1) ;
				support3.put(new IntTriple(triple), c) ;
			}
		}
	}

	private
	int predictPair(HashSet<Integer> profile, Integer q) {
		/* TODO: implement this method */
		return 0 ;
	}

	private
	int predictTriple(HashSet<Integer> profile, Integer q) {
		if (profile.size() < 2)
			return 0 ;

		int evidence = 0 ;
		for (Set<Integer> p : Sets.combinations(profile, 2)) {
			Integer den = support2.get(new IntPair(p)) ;
			if (den == null)
				continue ;

			TreeSet<Integer> t = new TreeSet<Integer>(p) ;
			t.add(q) ;
			IntTriple item = new IntTriple(t) ;			
			Integer num = support3.get(item) ;
			if (num == null)
				continue ;

			if (num.intValue() < min_supports)
				continue ;

			if ((double)num / (double)den >= threshold_3) 
				evidence++ ;
		}

		if (evidence >= min_evidence_3) 
			return 1 ;

		return 0 ;
	}	
}

class 
IntPair implements Comparable 
{
	int first ;
	int second ;

	public
	IntPair(int first, int second) {
		if (first <= second) {
			this.first = first ;
			this.second = second ;
		}
		else {
			this.first = first ;
			this.second = second ;
		}
	}

	public
	IntPair(Set<Integer> s) {
		Integer [] elem = s.toArray(new Integer[2]) ;
		if (elem[0] < elem[1]) {
			this.first = elem[0] ;
			this.second = elem[1] ;
		}
		else {
			this.first = elem[1] ;
			this.second = elem[0] ;
		}
	}

	public 
	int compareTo(Object obj) {
		IntPair p = (IntPair) obj ;

		if (this.first < p.first) 
			return -1 ;
		if (this.first > p.first)
			return 1 ;

		return (this.second - p.second) ;
	}
}

class 
IntTriple implements Comparable 
{
	int [] elem ;

	IntTriple(Set<Integer> s) {
		/* TODO: implement this method */
	}

	public 
	int compareTo(Object obj) {
		/* TODO: implement this method */
		return 0 ;
	}
}
