package doubleMatrix;

import org.jblas.DoubleMatrix;

public class Concat {

	public static DoubleMatrix Diagonal(DoubleMatrix m1, DoubleMatrix m2) {
		int r1 = m1.rows;
		int r2 = m2.rows;
		int c1 = m1.columns;
		int c2 = m2.columns;
		
		DoubleMatrix result = DoubleMatrix.zeros(r1+r2, c1+c2);
		
		int[] rows = new int[r1];
		for (int i = 0; i<r1; i++) rows[i]=i;
		int[] columns = new int[c1];
		for (int i = 0; i<c1; i++) columns[i]=i;
		result.put(rows, columns,m1);
		
		rows = new int[r2];
		for (int i = 0; i<r2; i++) rows[i]=r1+i;
		columns = new int[c2];
		for (int i = 0; i<c2; i++) columns[i]=c1+i;
		result.put(rows, columns,m2);
		
		return result;
	}
	
	public static DoubleMatrix Diagonal(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix m3) {
		int r1 = m1.rows; 
		int c1 = m1.columns;
		
		int r2 = m2.rows;
		int c2 = m2.columns;
		
		int r3 = m3.rows;
		int c3 = m3.columns;
		
		DoubleMatrix result = DoubleMatrix.zeros(r1+r2+r3, c1+c2+c3);
		
		int[] rows = new int[r1];
		for (int i = 0; i<r1; i++) rows[i]=i;
		int[] columns = new int[c1];
		for (int i = 0; i<c1; i++) columns[i]=i;
		result.put(rows, columns,m1);
		
		rows = new int[r2];
		for (int i = 0; i<r2; i++) rows[i]=r1+i;
		columns = new int[c2];
		for (int i = 0; i<c2; i++) columns[i]=c1+i;
		result.put(rows, columns,m2);
		
		rows = new int[r3];
		for (int i = 0; i<r3; i++) rows[i]=r1+r2+i;
		columns = new int[c3];
		for (int i = 0; i<c3; i++) columns[i]=c1+c2+i;
		result.put(rows, columns,m3);
		
		return result;
	}
	
}
