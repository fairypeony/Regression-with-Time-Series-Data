cd "C:\Users\User\Documents\Aplikasi Komputer\Data Bersih"
global data_dir "C:\Users\User\Documents\Aplikasi Komputer\Data Bersih"
*Mengatur direktori kerja untuk menyimpan atau mengambil data.

global data_in  "C:\Users\User\Documents\Aplikasi Komputer\Data Bersih"
global work  	"ch12-stock-returns-risk"

cap mkdir 		"C:\Users\User\Documents\Aplikasi Komputer\Data Bersih"
global output 	"C:\Users\User\Documents\Aplikasi Komputer\Data Bersih"

use "$data_in/stock-prices-daily.dta",clear
* Membuka file data Stata dengan lokasi disimpan di variabel global `data_in`.
* Opsi `clear` digunakan untuk menghapus data sebelumnya dari memori.

tsset date
* Mendefinisikan data sebagai time-series dengan variabel 'date' sebagai penanda waktu.

* EXPLORING DAILY TIME SERIES 

* prices
tsline p_MSFT, lcolor(navy*0.8) lwidth(medium) /// 
 ylab(0(20)120, grid) xlab(,grid) ///
 tlab(01jan1998 01jan2003 01jan2008 01jan2013 01jan2018) ///
 graphregion(fcolor(white) ifcolor(none))  ///
 plotregion(fcolor(white) ifcolor(white)) ///
 xtitle("Date (month)") ytitle(" Microsoft stock price (USD)")
graph export "$output/ch12-figure-2a-msft-daily-ts-Stata.png", replace
* Membuat grafik garis dari harga saham Microsoft (p_MSFT).
* Serta mengatur warna garis, lebar, label sumbu Y, sumbu X, dan area grafik.

tsline p_SP500, lcolor(navy*0.8) lwidth(medium) /// 
 ylab(500(500)3000, grid) xlab(,grid) ///
 tlab(01jan1998 01jan2003 01jan2008 01jan2013 01jan2018) ///
 graphregion(fcolor(white) ifcolor(none))  ///
 plotregion(fcolor(white) ifcolor(white)) ///
 xtitle("") ytitle(" S&P 500 stock market index")
graph export "$output/ch12-figure-2b-sp500-daily-ts-Stata.png", replace
* Menyimpan grafik ke direktori 'output' dengan nama file tertentu.

* EXPLORING MONTHLY TIME SERIES: PRICE LEVELS
keep if month!=month[_n+1] /* keep last day of month */
* Menyimpan hanya data untuk hari terakhir setiap bulan.
tab year
* Menampilkan tabulasi data berdasarkan tahun.
tsset ym
* Mendefinisikan ulang data sebagai time-series menggunakan variabel 'ym' (tahun-bulan).


* prices
tsline p_MSFT, lcolor(navy*0.8) lwidth(thick) /// 
 ylab(10(10)120, grid) xlab(,grid) ///
 tlab(1998m1 2003m1 2008m1 2013m1 2018m1) ///
 graphregion(fcolor(white) ifcolor(none))  ///
 plotregion(fcolor(white) ifcolor(white)) ///
 xtitle("") ytitle(" Microsoft stock price (USD)")
* Membuat grafik garis bulanan dari harga saham Microsoft dengan pengaturan serupa data harian.
graph export "$output/ch12-figure-3a-msft-monthly-ts-Stata.png", replace
* Menyimpan grafik bulanan ke direktori 'output'.

tsline p_SP500, lcolor(navy*0.8) lwidth(thick) /// 
 ylab(500(500)3000, grid) xlab(,grid) ///
 tlab(1998m1 2003m1 2008m1 2013m1 2018m1) ///
 graphregion(fcolor(white) ifcolor(none))  ///
 plotregion(fcolor(white) ifcolor(white)) ///
 xtitle("") ytitle(" S&P 500 stock market index")
* Membuat grafik garis untuk harga indeks bulanan S&P 500 dengan pengaturan visual.
graph export "$output/ch12-figure-3b-sp500-monthly-ts-Stata.png", replace
* Menyimpan grafik sebagai file PNG.


pperron p_MSFT
pperron p_SP500
* Melakukan uji stasioneritas philips perron untuk data harga saham Microsoft dan S&P 500.

* EXPLORING MONTHLY PERCENTAGE RETURNS
gen r_MSFT  =  100*(p_MSFT - p_MSFT[_n-1]) /p_MSFT[_n-1]
 lab var r_MSFT "Microsoft returns"
* Menghitung persentase return bulanan untuk Microsoft dan memberi label variabel.
gen r_SP500 =  100*(p_SP500 - p_SP500[_n-1]) /p_SP500[_n-1]
 lab var r_SP500 "SP500 returns"
* Menghitung persentase return bulanan untuk S&P 500 dan memberi label variabel.

tsline r_MSFT, lcolor(navy*0.8) lwidth(thick) /// 
 ylab(-40(10)40, grid) yline(0) xlab(,grid) ///
 tlab(1998m1 2003m1 2008m1 2013m1 2018m1) ///
 graphregion(fcolor(white) ifcolor(none))  ///
 plotregion(fcolor(white) ifcolor(white)) ///
 xtitle("") ytitle(" Microsoft monthly returns (percent)")
* Membuat grafik garis return bulanan Microsoft.
graph export "$output/ch12-figure-4a-msft-monthly-returns-Stata.png", replace
* Menyimpan grafik sebagai file PNG.

tsline r_SP500, lcolor(navy*0.8) lwidth(thick) /// 
 ylab(-40(10)40, grid) yline(0) xlab(,grid) ///
 tlab(1998m1 2003m1 2008m1 2013m1 2018m1) ///
 graphregion(fcolor(white) ifcolor(none))  ///
 plotregion(fcolor(white) ifcolor(white)) ///
 xtitle("") ytitle(" S&P 500 monthly returns (percent)")
* Membuat grafik garis return bulanan S&P 500.
graph export "$output/ch12-figure-4b-sp500-monthly-returns-Stata.png", replace
* Menyimpan grafik sebagai file PNG.

pperron r_MSFT
pperron r_SP500
* Melakukan uji stasioneritas philips perron untuk data return bulanan Microsoft dan S&P 500.

tabstat r_MSFT r_SP500, s(min max mean sd n) c(s) format(%3.2f)
** Menampilkan statistik deskriptif untuk return bulanan Microsoft dan S&P 500.

* REGRESSION, PERCENTAGE RETURNS
* LAKUKAN INSTALL OUTREG2
ssc install outreg2
* Menginstal ekstensi `outreg2` untuk menghasilkan tabel output regresi.
reg r_MSFT r_SP500, robust
* Melakukan regresi robust antara return Microsoft (dependent) dan return S&P 500 (independent).
 outreg2 using "$output/ch12-table-2-stocks-reg.tex",  tex(frag) dec(2) label 2aster replace 
* Mengekspor hasil regresi ke file tabel.
 
* visualizations of the regression
* scatterplot plus regression line
scatter r_MSFT r_SP500, ms(O) mc(navy*0.6) ///
 || lfit r_MSFT r_SP500, lw(vthick ) lc(green*0.8)  ///
 || line r_SP500 r_SP500, lc(black) lp(dash) ///
 ylab(-40(10)40, grid) xlab(-40(10)40, grid) ///
 xtitle(" S&P 500 monthly returns (percent)") ///
 ytitle(" Microsoft monthly returns (percent)") ///
 graphregion(fcolor(white) ifcolor(none))  ///
 plotregion(fcolor(white) ifcolor(white)) ///
 legend(order(2 3) label(2 "Regression line") label(3 "45 degree line"))
* Membuat scatterplot antara return Microsoft dan S&P 500 dengan garis regresi dan 45 derajat.
graph export "$output/ch12-figure-50-regression-Stata.png", replace
** Menyimpan scatterplot.

* time series jointly
* entire time period
tsline r_MSFT r_SP500, lc(navy*0.8 green*0.8) lw(medthick medthick) ///
 ylab(-40(10)40, grid) yline(0) ///
 tlab(1998m1 2003m1 2008m1 2013m1 2018m1, grid) ///
 graphregion(fcolor(white) ifcolor(none))  ///
 plotregion(fcolor(white) ifcolor(white)) ///
 xtitle("") ytitle(" Monthly returns (percent)")
* Membuat grafik garis return bulanan Microsoft dan S&P 500 bersama dalam satu grafik untuk seluruh periode.
graph export "$output/ch12-figure-6a-returns-together-Stata.png", replace
* Menyimpan grafik dalam format PNG.

* last two years
tsline r_MSFT r_SP500 if year>=2017, lc(navy*0.8 green*0.8) lw(medthick medthick) ///
 ylab(-12(4)12, grid) yline(0) tlab(, grid) ///
 graphregion(fcolor(white) ifcolor(none))  ///
 plotregion(fcolor(white) ifcolor(white)) ///
 xtitle("") ytitle(" Monthly returns (percent)")
* Membuat grafik garis return bulanan Microsoft dan S&P 500 bersama untuk dua tahun terakhir.
graph export "$output/ch12-figure-6b-returns-together-2017-9-Stata.png", replace
* Menyimpan grafik dalam format PNG.

* ADDITIONAL REGRESSIONS: LOG CHAGE, DAILY FREQ
* MONTHLY
use "$data_in/stock-prices-daily.dta",clear
* Membuka data harga saham harian.
* Or download directly from OSF:
/*
copy "https://osf.io/download/de8uc/" "workfile.dta"
use "workfile.dta", clear
erase "workfile.dta"
*/ 

keep if month!=month[_n+1] /* keep last day of month */
* Memilih hanya data hari terakhir dari setiap bulan.
tsset ym
* Mengatur data sebagai time series berdasarkan tahun dan bulan.

gen r_MSFT  =  100*(p_MSFT - p_MSFT[_n-1]) /p_MSFT[_n-1]
 lab var r_MSFT "Microsoft returns"
gen r_SP500 =  100*(p_SP500 - p_SP500[_n-1]) /p_SP500[_n-1]
 lab var r_SP500 "SP500 returns"
* Menghitung return bulanan untuk Microsoft dan S&P 500.
gen dlnp_MSFT = ln(p_MSFT) - ln(p_MSFT[_n-1])
gen dlnp_SP500 = ln(p_SP500) - ln(p_SP500[_n-1])
* Menghitung perubahan log harga saham Microsoft dan S&P 500.

*scatter dlnp_MSFT r_MSFT , xla(, grid) yla(, grid)
*scatter dlnp_SP500 r_SP500 , xla(, grid) yla(, grid)


reg r_MSFT r_SP500, robust
* Melakukan regresi robust antara return Microsoft dan S&P 500 (bulanan).
 outreg2 using "$output/ch12-table-3-stocks-reg.tex", tex(frag) dec(4) 2aster label ctitle("MSFT returns, monthly, pct change") replace 
replace r_MSFT = dlnp_MSFT /* to have the estimates in the same row in the table */
replace r_SP500 = dlnp_SP500 /* to have the estimates in the same row in the table */
reg r_MSFT r_SP500, robust
* Mengganti data untuk regresi menggunakan log change.
 outreg2 using "$output/ch12-table-3-stocks-reg.tex", tex(frag) dec(4) 2aster label ctitle("MSFT returns, monthly, log change") append
* Mengekspor hasil regresi log change ke file tabel


* DAILY:Y
use "$data_in/stock-prices-daily.dta",clear
* Membuka data harga saham harian.
* Or download directly from OSF:
/*
copy "https://osf.io/download/de8uc/" "workfile.dta"
use "workfile.dta", clear
erase "workfile.dta"
*/ 

tsset date
* Mengatur data sebagai time series berdasarkan tanggal.


gen r_MSFT  =  100*(p_MSFT - p_MSFT[_n-1]) /p_MSFT[_n-1]
 lab var r_MSFT "Microsoft returns"
gen r_SP500 =  100*(p_SP500 - p_SP500[_n-1]) /p_SP500[_n-1]
 lab var r_SP500 "SP500 returns"
* Menghitung return harian untuk Microsoft dan S&P 500.
gen dlnp_MSFT = ln(p_MSFT) - ln(p_MSFT[_n-1])
gen dlnp_SP500 = ln(p_SP500) - ln(p_SP500[_n-1])
* Menghitung perubahan log harga saham Microsoft dan S&P 500.

*scatter dlnp_MSFT r_MSFT , xla(, grid) yla(, grid)
*scatter dlnp_SP500 r_SP500 , xla(, grid) yla(, grid)

reg r_MSFT r_SP500, robust
 outreg2 using "$output/ch12-table-3-stocks-reg.tex", tex(frag) dec(4) 2aster label ctitle("MSFT returns, daily, pct change") append
* Melakukan regresi robust antara return harian Microsoft dan S&P 500, lalu menyimpan hasilnya.
 replace r_MSFT = dlnp_MSFT /* to have the estimates in the same row in the table */
replace r_SP500 = dlnp_SP500 /* to have the estimates in the same row in the table */
* Mengganti data untuk regresi menggunakan log change.
reg r_MSFT r_SP500, robust
 outreg2 using "$output/ch12-table-3-stocks-reg.tex", tex(frag) dec(4) 2aster label ctitle("MSFT returns, daily, log change") append
* Mengekspor hasil regresi log change ke file tabel
