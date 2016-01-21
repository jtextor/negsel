#ifndef REFINABLE_PARTITION_HPP
#define REFINABLE_PARTITION_HPP

/* Refinable partition */
class RefinablePartition{
public:
   int z, *E, *L, *S, *F, *P;

	RefinablePartition( int n ){
		z = bool( n );  E = new int[n];
		L = new int[n]; S = new int[n];
		F = new int[n]; P = new int[n];
		for( int i = 0; i < n; ++i ){
			E[i] = L[i] = i; S[i] = 0; 
		}
		if( z ){
			F[0] = 0; P[0] = n;
		}
	}
   
	~RefinablePartition(){
	   delete[] E;
	   delete[] L;
	   delete[] S;
	   delete[] F;
	   delete[] P;
	}

	void mark( int *M, int * W, int &w, int e ){
		int s = S[e], i = L[e], j = F[s]+M[s];
		E[i] = E[j]; L[E[i]] = i;
		E[j] = e; L[e] = j;
		if( !M[s]++ ){ W[w++] = s; }
	}

	void split( int *M, int * W, int &w  ){
		while( w ){
			int s = W[--w], j = F[s]+M[s];
			if( j == P[s] ){M[s] = 0; continue;}
			if( M[s] <= P[s]-j ){
				F[z] = F[s]; P[z] = F[s] = j;
			}
			else{
				P[z] = P[s]; F[z] = P[s] = j;
			}
			for( int i = F[z]; i < P[z]; ++i ){
				S[E[i]] = z;
			}
			M[s] = M[z++] = 0;
		}
	}
};

#endif