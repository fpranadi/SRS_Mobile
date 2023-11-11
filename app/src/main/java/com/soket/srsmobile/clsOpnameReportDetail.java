package com.soket.srsmobile;

public class clsOpnameReportDetail {
    private String namaBarang;
    private String kodePrroduk;
    private String opnameToko;
    private String opnameGudang;

    public void SetNamaBarang(String NamaBarang) {
        this.namaBarang = NamaBarang;
    }
    public String GetNamaBarang() {
        return namaBarang;
    }

    public void SetKodeProduk(String KodeProduk) {
        this.kodePrroduk = KodeProduk;
    }
    public String GetKodeProduk() {
        return kodePrroduk;
    }

    public void SetOpnameToko(String OpnameToko) {
        this.opnameToko = OpnameToko;
    }
    public String GetOpnameToko() {
        return opnameToko;
    }

    public void SetOpnameGudang(String OpnameGudang) { this.opnameGudang = OpnameGudang; }
    public String GetOpnameGudang() {
        return opnameGudang;
    }
}

