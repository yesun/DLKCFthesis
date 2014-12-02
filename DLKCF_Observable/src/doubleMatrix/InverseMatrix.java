package doubleMatrix;

import org.jblas.DoubleMatrix;
import org.jblas.Solve;

public class InverseMatrix {

	public static DoubleMatrix inv(DoubleMatrix A) {
		return Solve.solve(A, DoubleMatrix.eye(A.columns));
	}
	
	public static DoubleMatrix invPoSym(DoubleMatrix A) {
		return Solve.solvePositive(A, DoubleMatrix.eye(A.columns));
	}
	
}
