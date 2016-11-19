#Usage: ./NetMHCIIpan-3.1.pl [-h] [args] -f [fastafile/peptidefile]
#Command line options:
#
#PARAMETER       	DEFAULT VALUE                   	DESCRIPTION 
#[-rdir dirname] 	/home/nuzun/Tools/netMHCIIpan-3.1	Home directory for NetMHCIIpan
#[-tdir dirname] 	/home/nuzun/scratch/tmp_$$      	Temporary directory
#[-a name]       	DRB1_0101                       	HLA allele  
#[-choose]       	                                	Choose alpha and beta chains separately
#[-cha name]     	                                	Alpha chain name
#[-chb name]     	                                	Beta chain name
#[-affS float]   	50.000                          	Threshold for strong binders (IC50)
#[-affW float]   	500.000                         	Threshold for weak binders (IC50)
#[-rankS float]  	0.5                             	Threshold for strong binders (%Rank)
#[-rankW float]  	2                               	Threshold for weak binders (%Rank)
#[-filter]       	0                               	Toggle filtering of output
#[-affF float]   	500                             	Threshold for filtering output (IC50), if -filter option in on
#[-rankF float]  	2                               	Threshold for filtering output (%Rank), if -filter option in on
#[-hlaseq filename]	                                	File with full length MHC beta chain sequence (used alone for HLA-DR)
#[-hlaseqA filename]	                                	File with full length MHC alpha chain sequence (used with -hlaseq option)
#[-inptype int]  	0                               	Input type [0] FASTA [1] Peptide
#[-length int_array]	15                              	Peptide length. Necessary for FASTA input only.
#[-s]            	0                               	Sort output on descending affinity
#[-u]            	0                               	Print unique binding core only
#[-fast]         	0                               	Use fast mode (10 best networks)
#[-ex_offset]    	0                               	Exclude offset correction
#[-f filename]   	                                	File with the input data
#[-xls]          	0                               	Save output into xls file
#[-xlsfile filename]	NetMHCIIpan_out.xls             	File name for xls output
#[-dirty]        	0                               	Dirty mode, leave tmp dir+files
#[-w]            	0                               	w option for webface
#[-list]         	0                               	Print the list of possible alleles and exit
#[-h]            	0                               	Print this message and exit


#$1 = allele - DRB1_0101	
#$2 = peptide length - 15
#$3 = fasta seq - filename
#$4 = output - filename

#sorts...
#scripts/netMHCIIpan -a $1 -length $2 -s -f $3 > $4

scripts/netMHCIIpan -a $1 -length $2 -f $3 > $4
