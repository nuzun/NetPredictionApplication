# /home/naz/Programs/netMHCIIpan-2.0/Linux_i686/bin/netMHCIIpan -a DRB1_1101 -l 15 -s /home/naz/workspace/NetPanApplication/data/input/sequence/afp_P02771.fasta
# Fri Sep 16 13:45:12 2016
# User: naz
# PWD : /home/naz/workspace/NetPanApplication
# Host: Linux ubuntu 3.13.0-87-generic i686
# -a       DRB1_1101            HLA-DR allele (format DRBX_XXXX)
# -l       15                   Peptide length
# -s       1                    Sort output on descending affinity
# Command line parameters set to:
#	[-rdir filename]     /home/naz/Programs/netMHCIIpan-2.0/Linux_i686 Home directory for NetMHpan
#	[-syn filename]      /home/naz/Programs/netMHCIIpan-2.0/Linux_i686/data/synlist Synaps list
#	[-v]                 0                    Verbose mode
#	[-dirty]             0                    Dirty mode, leave tmp dir+files
#	[-sdev]              0                    Print sdev
#	[-tdir filename]     /home/naz/tmp/tmpNetMHCPanII Temporary directory (Default $$)
#	[-hlapseudo filename] /home/naz/Programs/netMHCIIpan-2.0/Linux_i686/data/HLA-DRB.pseudo File with HLA pseudo sequences
#	[-a line]            DRB1_1101            HLA-DR allele (format DRBX_XXXX)
#	[-f filename]                             File name with input
#	[-w]                 0                    w option for webface
#	[-s]                 1                    Sort output on descending affinity
#	[-p]                 0                    Use peptide input
#	[-th float]          50.000000            Threshold for high binding peptides
#	[-lt float]          500.000000           Threshold for low binding peptides
#	[-l int]             15                   Peptide length
#	[-xls]               0                    Save output to xls file
#	[-xlsfile filename]  NetMHCIIpan_out.xls  Filename for xls dump
#	[-u]                 0                    Print unique core only
#	[-listMHC]           0                    Print list of alleles included in netMHCpan
#	[-t float]           -99.900002           Threshold for output
#	[-thrfmt filename]   /home/naz/Programs/netMHCIIpan-2.0/Linux_i686/data/threshold/%s.thr_cmb Format for threshold filenames
#	[-exthr]             0                    Exclude threshold file from calculation
#	[-version filename]  /home/naz/Programs/netMHCIIpan-2.0/Linux_i686/data/version File with version information
#	[-inptype int]       0                    Input type [0] FASTA [1] Peptide

#$1 = allele - DRB1_0101	
#$2 = peptide length - 9
#$3 = fasta seq - filename
#$4 = output - filename

#scripts/netMHCIIpan -a $1 -l $2 -s $3 > $4
scripts/netMHCIIpan -a $1 -l $2 $3 > $4
