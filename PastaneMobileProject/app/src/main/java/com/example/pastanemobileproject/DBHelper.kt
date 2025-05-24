package com.example.pastanemobileproject

import java.security.MessageDigest
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "PastaneDB"
        private const val DATABASE_VERSION = 2 // Her güncellemede 1 artır!
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE kullanicilar (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ad TEXT,
                soyad TEXT,
                email TEXT,
                telefon TEXT,
                sifre TEXT
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE adresler (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                kullanici_id INTEGER,
                ilce TEXT,
                mahalle TEXT,
                posta_kodu TEXT,
                adres TEXT,
                FOREIGN KEY(kullanici_id) REFERENCES kullanicilar(id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE kategoriler (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                kategori_adi TEXT
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE urunler (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                urun_adi TEXT,
                aciklama TEXT,
                fiyat REAL,
                kategori_id INTEGER,
                FOREIGN KEY(kategori_id) REFERENCES kategoriler(id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE sepet (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                kullanici_id INTEGER,
                urun_id INTEGER,
                adet INTEGER,
                FOREIGN KEY(kullanici_id) REFERENCES kullanicilar(id),
                FOREIGN KEY(urun_id) REFERENCES urunler(id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE siparisler (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                kullanici_id INTEGER,
                siparis_zamani TEXT,
                siparis_durumu TEXT,
                FOREIGN KEY(kullanici_id) REFERENCES kullanicilar(id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE siparis_urunleri (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                siparis_id INTEGER,
                urun_id INTEGER,
                adet INTEGER,
                FOREIGN KEY(siparis_id) REFERENCES siparisler(id),
                FOREIGN KEY(urun_id) REFERENCES urunler(id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE odeme (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                siparis_id INTEGER,
                kart_adi TEXT,
                kart_numarasi TEXT,
                cvv TEXT,
                skt TEXT,
                FOREIGN KEY(siparis_id) REFERENCES siparisler(id)
            )
        """.trimIndent())

        // 🔥 ÖNEMLİ: Varsayılan verileri ekle
        insertDefaultCategories(db)
        insertDefaultProducts(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS kullanicilar")
        db.execSQL("DROP TABLE IF EXISTS adresler")
        db.execSQL("DROP TABLE IF EXISTS kategoriler")
        db.execSQL("DROP TABLE IF EXISTS urunler")
        db.execSQL("DROP TABLE IF EXISTS sepet")
        db.execSQL("DROP TABLE IF EXISTS siparisler")
        db.execSQL("DROP TABLE IF EXISTS siparis_urunleri")
        db.execSQL("DROP TABLE IF EXISTS odeme")
        onCreate(db)
    }

    private fun insertDefaultCategories(db: SQLiteDatabase) {
        val categories = listOf("Simit ve Poğaçalar", "Tatlılar", "Kurabiyeler", "İçecekler")
        for (category in categories) {
            val values = ContentValues()
            values.put("kategori_adi", category)
            db.insert("kategoriler", null, values)
        }
    }

    private fun insertDefaultProducts(db: SQLiteDatabase) {
        val urunler = listOf(
            // Simit ve Poğaçalar (kategori_id = 1)
            Triple("Susamlı Simit", "Taze fırından susamlı", 5.0),
            Triple("Kaşarlı Poğaça", "Eritilmiş kaşarlı poğaça", 8.0),
            Triple("Zeytinli Poğaça", "Siyah zeytinli yumuşak poğaça", 8.5),
            Triple("Açma", "Sade yumuşacık açma", 6.0),
            Triple("Peynirli Börek", "El açması beyaz peynirli", 10.0),
            Triple("Sosisli Poğaça", "Atıştırmalık sıcak poğaça", 9.0),

            // Tatlılar (kategori_id = 2)
            Triple("Mozaik Pasta", "Çikolatalı mozaik dilimi", 15.0),
            Triple("Cheesecake", "Frambuazlı dilim cheesecake", 20.0),
            Triple("Supangle", "Soğuk çikolatalı tatlı", 12.0),
            Triple("Profiterol", "Çikolata soslu profiterol", 18.0),
            Triple("Kazandibi", "Geleneksel yanık sütlü tatlı", 14.0),
            Triple("Fırın Sütlaç", "Fırında üstü kızarmış sütlaç", 13.0),

            // Kurabiyeler (kategori_id = 3)
            Triple("Un Kurabiyesi", "Ağızda dağılan klasik", 6.0),
            Triple("Cevizli Kurabiye", "Bol cevizli, ev yapımı", 7.5),
            Triple("Tarçınlı Kurabiye", "Tarçın kokulu yuvarlak", 7.0),
            Triple("Damla Çikolatalı", "Bol parçacıklı çikolatalı", 9.0),
            Triple("Hindistan Cevizli", "Kokulu, şekerli kurabiye", 8.0),
            Triple("Elmalı Kurabiye", "İç harçlı, pudra şekerli", 9.5),

            // İçecekler (kategori_id = 4)
            Triple("Türk Kahvesi", "Geleneksel köpüklü kahve", 10.0),
            Triple("Çay", "Demlikten taze çay", 5.0),
            Triple("Portakal Suyu", "Taze sıkma portakal", 12.0),
            Triple("Limonata", "Ferahlatıcı ev yapımı limonata", 9.0),
            Triple("Salep", "Tarçınlı sıcak salep", 11.0),
            Triple("Buzlu Latte", "Sütlü ve buzlu kahve", 15.0)
        )

        var kategoriId = 1
        var sayac = 0

        for ((adi, aciklama, fiyat) in urunler) {
            val values = ContentValues().apply {
                put("urun_adi", adi)
                put("aciklama", aciklama)
                put("fiyat", fiyat)
                put("kategori_id", kategoriId)
            }
            db.insert("urunler", null, values)
            sayac++
            if (sayac % 6 == 0) kategoriId++
        }
    }

    fun addUser(ad: String, soyad: String, email: String, telefon: String, sifre: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("ad", ad)
            put("soyad", soyad)
            put("email", email)
            put("telefon", telefon)
            put("sifre", sifre)
        }
        val result = db.insert("kullanicilar", null, values)
        db.close()
        return result != -1L
    }

    fun addAddress(kullaniciId: Int, ilce: String, mahalle: String, postaKodu: String, adres: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("kullanici_id", kullaniciId)
            put("ilce", ilce)
            put("mahalle", mahalle)
            put("posta_kodu", postaKodu)
            put("adres", adres)
        }
        val result = db.insert("adresler", null, values)
        db.close()
        return result != -1L
    }

    fun addPayment(kullaniciId: Int, kartAdi: String, kartNumarasi: String, cvv: String, skt: String): Boolean {
        val db = this.writableDatabase
        var odemeBasarili = false

        try {
            // 1️⃣ Sipariş oluştur
            val siparisValues = ContentValues().apply {
                put("kullanici_id", kullaniciId)
                put("siparis_zamani", System.currentTimeMillis().toString())
                put("siparis_durumu", "Hazırlanıyor")
            }
            val siparisId = db.insert("siparisler", null, siparisValues)

            if (siparisId == -1L) {
                db.close()
                return false
            }

            // 2️⃣ Ödeme kaydı oluştur
            val odemeValues = ContentValues().apply {
                put("siparis_id", siparisId)
                put("kart_adi", kartAdi)
                put("kart_numarasi", sha256(kartNumarasi))
                put("cvv", sha256(cvv))
                put("skt", sha256(skt))
            }

            val odemeId = db.insert("odeme", null, odemeValues)

            if (odemeId == -1L) {
                db.close()
                return false
            }

            // 3️⃣ Sepet ürünlerini sipariş_urunlerine aktar
            val cursor = db.rawQuery(
                "SELECT urun_id, adet FROM sepet WHERE kullanici_id = ?",
                arrayOf(kullaniciId.toString())
            )

            if (cursor.count == 0) {
                cursor.close()
                db.close()
                return false // Sepet boşsa sipariş oluşturulmasın
            }

            while (cursor.moveToNext()) {
                val urunId = cursor.getInt(0)
                val adet = cursor.getInt(1)

                val siparisUrunValues = ContentValues().apply {
                    put("siparis_id", siparisId)
                    put("urun_id", urunId)
                    put("adet", adet)
                }
                val result = db.insert("siparis_urunleri", null, siparisUrunValues)
                if (result == -1L) {
                    cursor.close()
                    db.close()
                    return false
                }
            }
            cursor.close()

            // 4️⃣ Sepeti boşalt
            db.execSQL("DELETE FROM sepet WHERE kullanici_id = ?", arrayOf(kullaniciId.toString()))
            odemeBasarili = true

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return odemeBasarili
    }

    fun addOrder(kullaniciId: Int): Long {
        val db = this.writableDatabase
        var siparisId: Long = -1

        try {
            // Yeni sipariş oluştur
            val siparisValues = ContentValues().apply {
                put("kullanici_id", kullaniciId)
                put("siparis_zamani", System.currentTimeMillis().toString())
                put("siparis_durumu", "Tamamlandı")
            }
            siparisId = db.insert("siparisler", null, siparisValues)

            // Sipariş başarılıysa, sepet içeriğini siparis_urunleri tablosuna aktar
            if (siparisId != -1L) {
                val cursor = db.rawQuery(
                    "SELECT urun_id, adet FROM sepet WHERE kullanici_id = ?",
                    arrayOf(kullaniciId.toString())
                )

                while (cursor.moveToNext()) {
                    val urunId = cursor.getInt(0)
                    val adet = cursor.getInt(1)

                    val siparisUrunValues = ContentValues().apply {
                        put("siparis_id", siparisId)
                        put("urun_id", urunId)
                        put("adet", adet)
                    }
                    val result = db.insert("siparis_urunleri", null, siparisUrunValues)

                    // Ürün ekleme başarısızsa hata ver
                    if (result == -1L) {
                        throw Exception("Ürün siparişe eklenemedi (urun_id: $urunId)")
                    }
                }
                cursor.close()

                // Sipariş alındıktan sonra sepeti boşalt
                db.execSQL("DELETE FROM sepet WHERE kullanici_id = ?", arrayOf(kullaniciId.toString()))
            } else {
                throw Exception("Sipariş oluşturulamadı (kullanici_id: $kullaniciId)")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return siparisId
    }


    fun getSiparisler(kullaniciId: Int): List<Map<String, Any>> {
        val db = this.readableDatabase
        val siparisList = mutableListOf<Map<String, Any>>()

        val cursor = db.rawQuery(
            """
        SELECT siparisler.id, siparis_zamani, siparis_durumu
        FROM siparisler
        WHERE kullanici_id = ?
        ORDER BY siparis_zamani DESC
        """.trimIndent(), arrayOf(kullaniciId.toString())
        )

        while (cursor.moveToNext()) {
            val siparisId = cursor.getInt(0)
            val siparisZamani = cursor.getString(1)
            val siparisDurumu = cursor.getString(2)

            siparisList.add(
                mapOf(
                    "id" to siparisId,
                    "zamani" to siparisZamani,
                    "durumu" to siparisDurumu
                )
            )
        }

        cursor.close()
        db.close()
        return siparisList
    }

    fun getSiparisDetaylari(siparisId: Int): Pair<List<Map<String, Any>>, Double> {
        val db = this.readableDatabase
        val detayList = mutableListOf<Map<String, Any>>()
        var toplamFiyat = 0.0

        val cursor = db.rawQuery(
            """
        SELECT urunler.urun_adi, urunler.aciklama, urunler.fiyat, siparis_urunleri.adet
        FROM siparis_urunleri
        INNER JOIN urunler ON siparis_urunleri.urun_id = urunler.id
        WHERE siparis_urunleri.siparis_id = ?
        """.trimIndent(), arrayOf(siparisId.toString())
        )

        while (cursor.moveToNext()) {
            val urunAdi = cursor.getString(0)
            val aciklama = cursor.getString(1)
            val fiyat = cursor.getDouble(2)
            val adet = cursor.getInt(3)
            val toplam = fiyat * adet

            detayList.add(
                mapOf(
                    "urunAdi" to urunAdi,
                    "aciklama" to aciklama,
                    "fiyat" to fiyat,
                    "adet" to adet,
                    "toplam" to toplam
                )
            )

            toplamFiyat += toplam
        }

        cursor.close()
        db.close()
        return Pair(detayList, toplamFiyat)
    }




    fun getProductsByCategoryWithPrice(kategoriAdi: String): List<Triple<String, String, Double>> {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT urun_adi, aciklama, fiyat 
        FROM urunler 
        WHERE kategori_id = (SELECT id FROM kategoriler WHERE kategori_adi = ?)
        """.trimIndent(), arrayOf(kategoriAdi)
        )
        val urunler = mutableListOf<Triple<String, String, Double>>()
        while (cursor.moveToNext()) {
            val ad = cursor.getString(0)
            val aciklama = cursor.getString(1)
            val fiyat = cursor.getDouble(2)
            urunler.add(Triple(ad, aciklama, fiyat))
        }
        cursor.close()
        db.close()
        return urunler
    }



    fun getSepetToplamFiyat(db: SQLiteDatabase, kullaniciId: Int): Double {
        var toplam = 0.0
        try {
            val cursor = db.rawQuery(
                """
            SELECT SUM(urunler.fiyat * sepet.adet)
            FROM sepet
            INNER JOIN urunler ON sepet.urun_id = urunler.id
            WHERE sepet.kullanici_id = ?
            """.trimIndent(), arrayOf(kullaniciId.toString())
            )
            if (cursor.moveToFirst()) {
                toplam = cursor.getDouble(0)
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return toplam
    }


    fun sepeteEkle(db: SQLiteDatabase, urunAdi: String, kullaniciId: Int) {
        val urunCursor = db.rawQuery("SELECT id FROM urunler WHERE urun_adi = ?", arrayOf(urunAdi))
        if (urunCursor.moveToFirst()) {
            val urunId = urunCursor.getInt(0)

            // Sepette ürün var mı kontrol et
            val sepetCursor = db.rawQuery(
                "SELECT adet FROM sepet WHERE kullanici_id = ? AND urun_id = ?",
                arrayOf(kullaniciId.toString(), urunId.toString())
            )
            if (sepetCursor.moveToFirst()) {
                val mevcutAdet = sepetCursor.getInt(0)
                db.execSQL(
                    "UPDATE sepet SET adet = ? WHERE kullanici_id = ? AND urun_id = ?",
                    arrayOf(mevcutAdet + 1, kullaniciId, urunId)
                )
            } else {
                db.execSQL(
                    "INSERT INTO sepet (kullanici_id, urun_id, adet) VALUES (?, ?, 1)",
                    arrayOf(kullaniciId, urunId)
                )
            }
            sepetCursor.close()
        }
        urunCursor.close()
    }

    fun updateSiparisDurumu(siparisId: Int, yeniDurum: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("siparis_durumu", yeniDurum)
        }
        db.update("siparisler", values, "id = ?", arrayOf(siparisId.toString()))
        db.close()
    }

    fun getSiparisInfo(siparisId: Int): Map<String, String> {
        val db = this.readableDatabase
        val info = mutableMapOf<String, String>()

        // Sipariş zamanı ve durumu
        val siparisCursor = db.rawQuery("SELECT siparis_zamani, siparis_durumu, kullanici_id FROM siparisler WHERE id = ?", arrayOf(siparisId.toString()))
        var kullaniciId: Int? = null
        if (siparisCursor.moveToFirst()) {
            val zaman = siparisCursor.getString(0)
            val durum = siparisCursor.getString(1)
            kullaniciId = siparisCursor.getInt(2)
            info["zaman"] = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(zaman.toLong()))
            info["durum"] = durum
        }
        siparisCursor.close()

        // Adres bilgisi
        if (kullaniciId != null) {
            val adresCursor = db.rawQuery(
                "SELECT ilce, mahalle, posta_kodu, adres FROM adresler WHERE kullanici_id = ? ORDER BY id DESC LIMIT 1",
                arrayOf(kullaniciId.toString())
            )
            if (adresCursor.moveToFirst()) {
                val adresStr = "${adresCursor.getString(1)}, ${adresCursor.getString(0)} - ${adresCursor.getString(2)}\n${adresCursor.getString(3)}"
                info["adres"] = adresStr
            }
            adresCursor.close()
        }

        // Ödeme bilgisi
        val odemeCursor = db.rawQuery(
            "SELECT kart_adi FROM odeme WHERE siparis_id = ?",
            arrayOf(siparisId.toString())
        )
        if (odemeCursor.moveToFirst()) {
            val kartAdi = odemeCursor.getString(0)
            info["odeme"] = if (kartAdi.lowercase().contains("kapı")) "Kapıda Ödeme" else "Kartla Ödeme"
        } else {
            info["odeme"] = "Kapıda Ödeme"
        }
        odemeCursor.close()

        db.close()
        return info
    }
    fun getKullaniciAdi(kullaniciId: Int): String {
        val db = this.readableDatabase
        var ad = "Kullanıcı"
        try {
            val cursor = db.rawQuery("SELECT ad FROM kullanicilar WHERE id = ?", arrayOf(kullaniciId.toString()))
            if (cursor.moveToFirst()) {
                ad = cursor.getString(0)
                println("✅ Kullanıcı adı bulundu: $ad (id: $kullaniciId)")
            } else {
                println("❌ Kullanıcı bulunamadı (id: $kullaniciId)")
            }
            cursor.close()
        } catch (e: Exception) {
            println("❌ HATA getKullaniciAdi(): ${e.message}")
        } finally {
            db.close()
        }
        return ad
    }

    // Kullanıcı bilgilerini getir
    fun getKullaniciBilgileri(kullaniciId: Int): Map<String, String> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT ad, soyad, telefon FROM kullanicilar WHERE id = ?", arrayOf(kullaniciId.toString()))
        val result = mutableMapOf<String, String>()
        if (cursor.moveToFirst()) {
            result["ad"] = cursor.getString(0)
            result["soyad"] = cursor.getString(1)
            result["telefon"] = cursor.getString(2)

        }
        cursor.close()
        db.close()
        return result
    }

    // Kullanıcı bilgilerini güncelle
    fun updateKullaniciBilgileri(kullaniciId: Int, ad: String, soyad: String, telefon: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("ad", ad)
            put("soyad", soyad)
            put("telefon", telefon)

        }
        val result = db.update("kullanicilar", values, "id = ?", arrayOf(kullaniciId.toString()))
        db.close()
        return result > 0
    }

    // Adres bilgilerini getir
    fun getAdres(kullaniciId: Int): Map<String, String>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT ilce, mahalle, posta_kodu, adres FROM adresler WHERE kullanici_id = ? ORDER BY id DESC LIMIT 1", arrayOf(kullaniciId.toString()))
        var result: Map<String, String>? = null
        if (cursor.moveToFirst()) {
            result = mapOf(
                "ilce" to cursor.getString(0),
                "mahalle" to cursor.getString(1),
                "posta_kodu" to cursor.getString(2),
                "adres" to cursor.getString(3)
            )
        }
        cursor.close()
        db.close()
        return result
    }

    // Adresi güncelle
    fun updateAdres(kullaniciId: Int, ilce: String, mahalle: String, postaKodu: String, adres: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("ilce", ilce)
            put("mahalle", mahalle)
            put("posta_kodu", postaKodu)
            put("adres", adres)
        }
        val result = db.update("adresler", values, "kullanici_id = ?", arrayOf(kullaniciId.toString()))
        db.close()
        return result > 0
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun getAdresListesi(kullaniciId: Int): List<Map<String, String>> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id, ilce, mahalle, posta_kodu, adres FROM adresler WHERE kullanici_id = ?", arrayOf(kullaniciId.toString()))
        val result = mutableListOf<Map<String, String>>()
        while (cursor.moveToNext()) {
            result.add(
                mapOf(
                    "id" to cursor.getInt(0).toString(),
                    "ilce" to cursor.getString(1),
                    "mahalle" to cursor.getString(2),
                    "posta_kodu" to cursor.getString(3),
                    "adres" to cursor.getString(4)
                )
            )
        }
        cursor.close()
        db.close()
        return result
    }

    fun updateAdresField(adresId: Int, field: String, value: String): Boolean {
        val db = this.writableDatabase
        val query = "UPDATE adresler SET $field = ? WHERE id = ?"
        db.execSQL(query, arrayOf(value, adresId.toString()))
        db.close()
        return true
    }



}
