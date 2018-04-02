package uk.ac.bbk.cryst.netprediction.main;

public class AlloimmunityProgram {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// input: netctlpan allele list for humans
		// input: HLA A, B, C sequence files
		
		// read HLA A, B or C file
		// foreach HLA sequence read and allele read 
			// run netctlpan 
			// if there are not any good binders then not novel
			// else if there are good binders then 
				// for each good binders do
					// find the allele sequence from the full file
					// if there is a match to the binder with good aff then not novel
					// else do proteome check for the binder
						// if we have a match with good aff then not novel
						// else novel and add the binder to the novel list
		
		// From the novel list identify the best affinity score 
		// that is the score for the square (HLA sequence, netctlpan allele)
		 
		
	}

}
