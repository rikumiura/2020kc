main() {
   int i, j, n;

   for (i=1; i<11; i++) {
      n=0;
      for (j=1; j<i+1; ++j) {
         n += j;
      }
      outputint (n);
   }
}
