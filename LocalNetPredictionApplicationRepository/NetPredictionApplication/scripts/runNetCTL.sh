# Fri Oct 13 11:49:10 2017
# User: nuzun
# PWD : /home/nuzun/Tools/netCTL-1.2b
# Host: Linux ubuntu 4.4.0-87-generic x86_64
# -s       A1                   Supertype
# -f       test/test.fsa        File name with input
# -sort    0                    Sort output on score: 0 [comb], 1 [MHC], 2 [Cle], 3 [TAP] <0 No sort
# Command line parameters set to:
#       [-v]                 0                    Verbose mode
#       [-c filename]        do_clecmd.epipred    Cleavage prediction code
#       [-t filename]        do_tapmat1-9.endert  Tap prediciton code
#       [-l filename]        /home/nuzun/Tools/netCTL-1.2b/Linux_x86_64/lib/ Library directory
#       [-m filename]        /home/nuzun/Tools/netCTL-1.2b/Linux_x86_64/lib/data/method.list.CTL Method list file
#       [-wt float]          0.050000             Weight of tap
#       [-we float]          1.000000             Weight of MHC
#       [-wc float]          0.150000             Weight of Clevage
#       [-dirty]             0                    Dirty mode
#       [-tdir filename]     /home/nuzun/scratch/netCTL-1.2b Temporary directory (Default $$)
#       [-bdir filename]     /home/nuzun/Tools/netCTL-1.2b/Linux_x86_64/bin/ Binary directory
#       [-nihbin filename]   /home/nuzun/Tools/netCTL-1.2b/Linux_x86_64/bin/nihpred_netCTL NIHpred binary
#       [-nihlib filename]   /home/nuzun/Tools/netCTL-1.2b/Linux_x86_64/data NIH lib
#       [-s filename]        A1                   Supertype
#       [-sort int]          0                    Sort output on score: 0 [comb], 1 [MHC], 2 [Cle], 3 [TAP] <0 No sort
#       [-thr float]         0.750000             Threshold for identifying epitopes
#       [-longid]            0                    Print long sequence id [Default is short]
#       [-w]                 0                    w option for webface
#       [-f filename]        test/test.fsa        File name with input
#NetCTL-1.2 predictions using MHC supertype A1. Threshold 0.750000
#
#  55 ID gi|33331470| pep ISERILSTY aff   0.4989 aff_rescale   2.1184 cle 0.9764 tap   2.8980 COMB   2.4098 <-E
#  67 ID gi|33331470| pep STEPVPLQL aff   0.2059 aff_rescale   0.8743 cle 0.9522 tap   0.9740 COMB   1.0658 <-E
#  15 ID gi|33331470| pep AVRIIKILY aff   0.1170 aff_rescale   0.4966 cle 0.9739 tap   3.3570 COMB   0.8105 <-E
#  20 ID gi|33331470| pep KILYKSNPY aff   0.0892 aff_rescale   0.3787 cle 0.9327 tap   3.1870 COMB   0.6780
#You most specify input file either by using -f or as /home/nuzun/Tools/netCTL-1.2b/Linux_x86_64/bin/netCTL [args] fastafile
#Usage: netCTL [-h] [args] [fastafile]

#$1 = allele - A1	
#$2 = fasta seq - filename
#$3 = sort - 0
#$4 = output - filename

scripts/netCTL -s $1 -f $2 -sort $3 > $4
