#!/bin/bash 
#システムプロジェクトI レポート提出用スクリプト by Toru Kato 2006.9.9
#提出しようとしているプログラムがJavaプログラムの場合，各自のノートパソコン
#上でコンパイルし，コンパイルが成功した場合に限り ssh を使ってOSECのファイル
#サーバの自分のホームディレクトリにコピーする．その後，OSECにある提出用プロ
#グラムに処理をまかせる．

#2006.5.9改変
#入力を促すメッセージでのechoに -n オプションを付け、入力しやすいようにした。

#2006.10.31 改変 pdfファイルの提出にも対応した．

#2007.4.14 改変 2007年度から統合された新UNIXサーバに対応．17号館と38号館か
#らでは違うサーバアドレスになる(実体は同じ)．

#2009.4.3 改変 全ての資料やプログラムは全て ~/projI10 にあることを前提に
#コマンドを書き換えた．提出するプログラムは  ~/projI10/kc にコピーしてから
#このスクリプトを使用する必要がある．

#2010.4.3 改変 ProjI10というプロジェクトがworkspaceにあることを前提に
#提出コマンドを書き換えた。
#~/workspace/ProjI10/src/kc にあるjavaファイルをコンパイルし、
#~/projI10/kc にクラスファイルを生成する。コンパイルが成功すれば
#scpでファイルサーバのホームディレクトリにコピーし、向こうで提出箱に
#提出処理を行う。

#2013.3.27 改変。2013年度から学生のPCがMacになるので、ディレクトリ構成を
#MacOS向けに変更。



###############################################################
###############################################################
############                                  #################
############              警  告              #################
############                                  #################
############  このファイルには一切変更を加え  #################
############  てはいけません．レポートの提出  #################
############  が不可能になります．            #################
############                                  #################
###############################################################
###############################################################


###############################################################
####               変数定義部       ###########################
###############################################################
#年度
YEAR=20

#ファイルサーバ名
UNIX38=163.51.38.75

#レポート番号の最大値
MaxRepnum=8

#情報システムプロジェクト用ディレクトリ
KCDIR=~/Documents/projI$YEAR

#提出用プログラム名
SUBMIT=/home/linuxfs/learning/prjI/bin/submitK09
SUBMIT2=/home/linuxfs/learning/prjI/bin/submitK09_2

#コンパイル用クラスパス
WORKSRC=~/Documents/workspace/ProjI$YEAR/src/kc
WORKBIN=~/Documents/workspace/ProjI$YEAR/bin
DESTPATH=$KCDIR
DESTPATHsrc=$KCDIR/kc


###############################################################
####           補助関数定義部       ###########################
###############################################################

printWorknum() #作業内容選択メッセージ出力用関数
{
   echo
   echo "作業番号を入力してください．"
   echo "0 終了"
   echo "1 課題ファイル提出(コンパイル確認のため単一ファイルのみ指定)"
   echo "2 提出済み課題ファイル確認"
   echo "3 提出済み課題ファイル削除"
   echo "4 授業開始及び終了時のファイル提出(複数指定可, スペース区切り)"
}

codeConv() #ファイルの文字コードを変換する関数
{
  if [ -f tmpfile ]
   then
     rm tmpfile
  fi
  nkf -dw80 "$1" > tmpfile
  mv tmpfile $1
}

compileJava() #Javaコンパイル用関数．コンパイルに失敗したら全体の処理停止．
{
  javac -encoding utf-8 -classpath $WORKBIN -d $DESTPATH $1
  if [ $? -ne 0 ]
   then
     echo $1 がコンパイルできないので，提出はできません．
     exit 0
  fi
}


###############################################################
####処理スタート#### main関数に相当 ###########################
###############################################################

#引数が1個でない場合は，使い方を表示して終了．
if [ $# -ne 1  ]
then
     echo 使用方法
     echo  submitK$YEAR.sh  あなたの情報処理演習室やOSECでのアカウント名
     exit 0
fi

MyServer=$UNIX38


#第一引数として与えられたアカウント名を確認して，処理スタート
echo あなたの情報処理演習室やOSECでのアカウント名は
echo $1であっていますか?  違う場合は0を入力して終了してください．
echo
echo


for ((i = 0; $i != 1; )) #1,2,3 の処理を，0が入力されるまで繰り返す．
do
  printWorknum
  read -p "作業番号? " Number
  case $Number in
     0)
        echo 作業を終了します.
        exit 0
        ;;
     1) # 提出処理．レポート番号とファイル名の入力を促す．
        echo レポート番号とファイル名を入力してください．
        echo -n レポート番号?　
        read RepNum

        if [ $RepNum -ge 1 -a $RepNum -le $MaxRepnum ]
           then  
             echo -n ファイル名?　
             read FileName
             for prog in $FileName
                do 
                   if [ ! -f $WORKSRC/$prog ]  #ファイルの存在確認．
                    then
                      echo ファイル $WORKSRC/$prog が存在しません.
                      echo ファイル名を確認してからやり直してください．
                      exit 0
                   else
                      for javaProg in $WORKSRC/*.java
                         do                  
                            if [ $javaProg == $WORKSRC/$prog ]  #Java プログラムかどうか確認し，
                             then           
                              compileJava $javaProg  #Java プログラムの場合のみコンパイル
                            fi
                         done # end of javaProg in *.java
                   fi
             done #end of  for prog in $FileName
             echo $WORKSRC/$FileName を提出します．
             #myServer $1
             scp  $WORKSRC/$FileName $1@$MyServer:~/  
             ssh $MyServer -l $1 "cd ~; chmod g+x .; chmod g+r $FileName; $SUBMIT $RepNum $FileName; rm $FileName; chmod g-x .; exit"
         else 
           echo $RepNum はレポート番号としては正しくありません．
           echo やり直してください．
         fi #of if [ $RepNum -ge 1 -a $RepNum -le $MaxRepnum ] 

        #i=1  #for ((i = 0; $i != 1; )) ループを終了させる．
        ;;
     2) #レポート確認処理。指定されたレポート番号の提出箱の中にあるファイルを表示。
        echo レポート番号を入力してください．
        echo  -n レポート番号?　
        read RepNum
        if [ $RepNum -ge 1 -a $RepNum -le $MaxRepnum ]
           then  
             #myServer $1
             ssh $MyServer -l $1 "$SUBMIT $RepNum; exit"
        else 
           echo $RepNum はレポート番号としては正しくありません．
           echo やり直してください．
        fi #of if [ $RepNum -ge 1 -a $RepNum -le $MaxRepnum ] 

     
        #i=1  #for ((i = 0; $i != 1; )) ループを終了させる．
        ;;
     3)  #提出済みのファイルを削除する処理。
        echo レポート番号とファイル名を入力してください．
        echo -n レポート番号?　
        read RepNum
        if [ $RepNum -ge 1 -a $RepNum -le $MaxRepnum ]
           then  
              echo  -n ファイル名?　
              read FileName
              #myServer $1
              ssh $MyServer -l $1 "$SUBMIT -d $RepNum $FileName; exit"
        else 
              echo $RepNum はレポート番号としては正しくありません．
              echo やり直してください．
        fi #of if [ $RepNum -ge 1 -a $RepNum -le $MaxRepnum ] 
 

        #i=1  #for ((i = 0; $i != 1; )) ループを終了させる．
        ;;
     4) # 授業開始時及び終了時のファイル提出処理．レポート番号とファイル名の入力を促す．
        echo レポート番号とファイル名を入力してください．
        echo -n レポート番号?　
        read RepNum
        JAVAorPDF=null

        if [ $RepNum -ge 1 -a $RepNum -le $MaxRepnum ]
           then  
             echo -n ファイル名?　
             read FileName
             echo 
             if [ ! -f $WORKSRC/$FileName -a ! -f $PDFPATH/{$FileName} ]  #ファイルの存在確認．
                    then
                      echo ファイル $FileName が，既定のディレクトリ内に存在しません.
                      echo java ファイルは $WORKSRC にあるもののみが提出可能です．
                      echo ファイル名やファイルを作成した場所を確認してからやり直してください．
             else #正常提出作業開始
		     CWD=`pwd`
		     cd $WORKSRC
		     echo $DESTPATHsrc
                     cp $FileName $DESTPATHsrc
                     echo $FileName を提出します．
		     cd $DESTPATHsrc
                     scp $FileName $1@$MyServer:~/
                     ssh $MyServer -l $1 "cd ~/; chmod go+x .; chmod go+r $FileName; $SUBMIT2 $RepNum $FileName; rm $FileName; chmod go-x .; exit"
		     cd $CWD
             fi   #end of ファイルの存在確認．

         else 
           echo $RepNum はレポート番号としては正しくありません．
           echo やり直してください．
         fi #of if [ $RepNum -ge 1 -a $RepNum -le $MaxRepnum ] 
        ;;


       *)
        echo;echo;
        echo 0〜4の数字を入力してください．
        # i=1 が無いので，for ((i = 0; $i != 1; )) ループを継続する．
      esac 
done #end of for ((i = 0; $i != 1; ))
