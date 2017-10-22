# /home/nuzun/Tools/netCTLpan-1.1/Linux_x86_64/bin/netCTLpan -a HLA-A02:01 -l 9 -s 0 -f test.fsa
# Fri Oct 13 14:19:07 2017
# User: nuzun
# PWD : /home/nuzun/Tools/netCTLpan-1.1/test
# Host: Linux ubuntu 4.4.0-87-generic x86_64
# -a       HLA-A02:01           HLA allele
# -l       9                    Peptide length [8-11]
# -s       0                    Sort output on score: 0 [comb], 1 [MHC], 2 [Cle], 3 [TAP] <0 No sort
# -f       test.fsa             File name with input
# Command line parameters set to:
#       [-rdir filename]     /home/nuzun/Tools/netCTLpan-1.1/Linux_x86_64 Home directory for NetMHpan
#       [-c filename]        /home/nuzun/Tools/netCTLpan-1.1/Linux_x86_64/bin/clepred Cleavage prediction code
#       [-t filename]        /home/nuzun/Tools/netCTLpan-1.1/Linux_x86_64/bin/tapmat_pred_fsa Tap prediciton code
#       [-m filename]        /home/nuzun/Tools/netCTLpan-1.1/netMHCpan-2.3//netMHCpan MHC binding prediction code
#       [-v]                 0                    Verbose mode
#       [-dirty]             0                    Dirty mode, leave tmp dir+files
#       [-tdir filename]     /home/nuzun/scratch/netCTLpan-1.1 Temporary directory (Default $$)
#       [-hlaseq filename]                        File with full length HLA sequences
#       [-a line]            HLA-A02:01           HLA allele
#       [-f filename]        test.fsa             File name with input
#       [-s int]             0                    Sort output on score: 0 [comb], 1 [MHC], 2 [Cle], 3 [TAP] <0 No sort
#       [-l int]             9                    Peptide length [8-11]
#       [-xls]               0                    Save output to xls file
#       [-xlsfile filename]  NetCTLpan_out.xls    Filename for xls dump
#       [-thr float]         -99.900002           Threshold for output
#       [-listMHC]           0                    Print list of alleles included in netMHCpan
#       [-thrfmt filename]   /home/nuzun/Tools/netCTLpan-1.1/Linux_x86_64/data/threshold/%s.thr Format for threshold filenames
#       [-wt float]          0.025000             Weight of tap
#       [-wc float]          0.225000             Weight of Clevage
#       [-ethr float]        1.000000             Threshold for epitopes
#       [-version filename]  /home/nuzun/Tools/netCTLpan-1.1/Linux_x86_64/data/version File with version information

# NetCTLpan version 1.1


# Peptide length 9
# NetCTLpan predictions for HLA-A*02:01 allele.

#  N   Sequence Name       Allele      Peptide      MHC      TAP      Cle     Comb  %Rank
# 219 143B_BOVIN_(P29  HLA-A*02:01    QLLRDNLTL  0.42500  1.04100  0.97391  0.67015   3.00 


#$1 = allele - HLA-A02:01 	
#$2 = peptide length - 9
#$3 = sort - 0
#$4 = fasta seq - filename
#$5 = output - filename

scripts/netCTLpan -a $1 -l $2 -s $3 -f $4 > $5
