# TTÜ E-Sport

Reeglid tulevaseks võrgupeoks, mis toimub 9.-11. veebruar
## 1. Üldiselt

Need reeglid baseeruvad ESL 5vs5 avatud liiga reeglistikule, kuid neid on muudetud vastavalt TTÜ e-spordi vajadustele.
Järgmised reeglid on ausa mängu tagamiseks.

Mänguvana (GM) võib igal hetkel muuta neid reegleid.

Mängijate poolsed muudatused tuleb enne läbi rääkida GM ja tiimide vahel. Sellised kokkulepped tuleb ülesse kirjutada.
Siia alla ei lähe muudatused, mida otsustab GM iseseisvalt, et tagada mängjate vahelist võrdsust.

### 1.1 Nõustumised

* GM võib muuta reegleid vastavalt oma soovile.
* Iga mängija peab olema tutvunud reeglitega enne võrgupeo algust.
* Võrgupeol osalejad nõustuvad nende reeglitega.
* Terve tiim vastutab oma liikmete eest - isegi, kui üks liikmetest rikub reegleid, siis karistada saavad kõik.

### 1.2 Koodimine

* Mängijad, kes jäävad koodimisega vahele diskvalitseeritakse võistlusest koos tiimiga
* Olenevalt olukorrast võib rakenduda keeld edaspidistele turniiridele, mis on korraldatud TTÜ e-spordi poolt
* Koodimise alla kuuluvad aimbots, wallhacks, sound hacks, no recoil, no smoke, no flash, colored models
ambient occlusion ja teised kolmanda osapoole programmid.

### 1.2.1 Keelu tühistamine

* Olenevalt olukorrast võib turniiril osalemise keelu tühistada, kui on näha, et mängija on õppinud eelmisest korrast

## 2. Demod

Igal mängijal on **kohustus** salvestada enda vaatepunktist (POV) kogu mäng. Juhul, kui GM või korraldusmeeskond 
küsib koodimise põhjustelseda salvestust ja seda pole, siis võib GM anda teisele tiimile võidu. 
GM võib kontrollida iga mängija demosi

### 2.1 Salvestuste päring

Mängija ei ole kohustatud saatma enda POV salvestust teisele mängijale. Koodimise kahtlusel peab sellest koheselt 
teavitama korraldusmeeskonda või GM-i. GM võib neid demosi jagada vajaduspõhiselt.

### 2.2 Salvestuste üleslaadimine

Salvestused peavad olema arhiivis (.zip, .rar). Salvestused laetakse ülesse võrgupeol olevasse serverisse.
Neid ei tohi laadida internetti, kui neid küsitakse GM-i poolt. Salvestusi ei tohi muuta.

### 2.3 Koodimise süüdistus

Süüdistused võetakse arvesse ainult võrgupeo ajal. Vajadusel kontrollib seda GM. 
Hilisemad süüdistused ei lähe arvesse.

## 3. Seaded

### 3.1 Mängijate seaded

### 3.1.1 Keelatud scriptid

Kui sa ei ole kindel, kas mingi script on lubatud, siis see ilmselt ei ole.
Siin on mõned näited. 

* Lua scripts
* Jumpthrow
* Stop shoot scripts (Use or AWP scripts)
* Center view scripts
* Turn scripts (180° or similar)
* No recoil scripts
* Burst fire scripts
* Rate changers (Lag scripts)
* FPS scripts
* Anti-flash scripts or binding (snd_* bindings)
* Bunnyhop scripts
* Stop sound scripts

Nende kasutamisel tuleb esimene kord hoiatus. Järgmisel korral tähendab see mängu kaotust.
### 3.1.2 Lubatud scriptid

* Relvade ostu scriptid
* Salvestamise scriptid
* lülitus scriptid, mis ei muuda mängu

### 3.1.3 Mängu sisesed kuvad

Mängu sisesed kuvad on üldiselt keelatud v.a. järgmised:
* cl_showpos 1
* cl_showfps 1
* net_graph 0/1 (1 puhul ei tohi seda ära kasutada sihtimiseks)

### 3.1.4 Failide muutmine

HUD-i võib muuta, kui see ei muuda kuvatud informatsiooni hulka võrreldes originaaliga.
Faile võib muuta ainult HUDi ja GUI.

Ülejäänud failide muutmine on keelatud.

Mõned näited:
* Muudetud mudelid
* Muudetud tekstuurid
* Muudetud helid

### 3.1.5 Tarkvara ja riistvara tööriistad

Muudatused graafika ja tekstuuride kohta on keelatud. Kuvad on samuti keelatud
(Nvidia SLI Display, Rivatuner Overlays). Tööriistad, mis näitavad **ainult** FPS-i on lubatud.

Ülalmainitud tööriistada kasutamine on keelatud

Mängu jooksutamine 16-biti värvides on keelatud. 
Lisaks on keelatud muuta mat_hdr_enabled ja mat_hdr_level väärtusi.

### 3.1.6 Config kasuta sisu

Selles kaustas võib olla ainult configu failid, kõik muu on keelatud.

## 4. Mängu reeglid

### 4.1 Mängunimi

Mängunimes ei tohi olla ebasündsust.

### 4.2 Soojendus

Mängu alustasmiseks tuleb kirjutada mängus .ready

### 4.3 Mängijad mängus

Kuna võistlus toimub 5vs5, siis mängu alustamiseks peavad kõik 10 mängijat kohal olema.

Ainult mängijad, kes on kinnitatud enne võistluse algust võivad mängida.

### 4.4 Võistluse ajal

### 4.4.1 Poole valik

Poole otsustamiseks tehakse serveri poolne automaatne noaround. 
Võitja tiim otsustab kumbalt poolt nad otsutavad mängida kirjutades .stay või .switch

### 4.4.2 Serveri crash
Juhul, kui server jookseb kokku esimese 3 roundi juures, siis alustatakse mäng uuesti 0-0.

Juhul, kui server jookseb kokku pärast seda, siis GM proovib viimase roundi taastada
Võimalusel, et GM ei saa roundi taastatud, siis mängitakse edasi viimasest roundist ja võitja tiim alustab 2000
ja kaotaja tiim peab kirjutama konsooli kill, seda roundi ei võeta arvesse ja kaotaja algusrahaks tuleb 800

### 4.4.3 Mängija lahkumine

Juhul, kui mängija kaob mängust, siis tuleb kirjutada mängus .pause

Mängijal on aega 15 minutit, et tagasi tulla. Pärast seda mängitakse edasi isegi, kui mängija ei jõua selle aja sees
tagasi.


### 4.4.4 Mängijate vahetus

Mängijaid ei tohi vahetada turniiri jooksul.

### 4.4.5 Lisaaeg

Juhul, kui mäng jääb viiki, siis ei tule lisaaega. Pärast grupimänge toimub lisaaeg.

### 4.4.6 Mapide valimine

Parim ühest: Ban, ban, ban, ban, ban, ban ja valik
Parim kolmest: Ban, ban, valik, valik, ban, ban ja valik

### 4.4.7 Vead ja ja keelatud tegevused

### 4.4.7.1 Pomm

Mängijad ei tohi paigaldada pommi ligipääsematutesse kohtadesse

### 4.4.7.2 Boosting, Sky walking, sharking

Boostimine läbi seinade, lae või maa, kõndimine nähtamatutel äärtel, "Skywalking", "Map swimming", ja "Sharking" on
kõik keelatud tegevused

Tavaline inimeste kuhjamine on lubatud

### 4.4.7.3 Muu

Vigade kasutamine on keelatud. Uue vea leidmisel GM otsustab karistuse

### 4.5 Juhendamine

Juhendamine on lubatud, kui juhendaja on ennast tiimiga registreerinud

Juhendajal on järgmised õigused ja reeglid:
* Juhendaja võib kutsuda pausi
* Juhendaja ei tohi olla füüsilises kontaktis mängijatega
* Juhendaja ei tohi olla arvutis mängu ajal
* Juhendaja tohib olla samas kommunikeerimiskanalis, kus on mängijad
* Juhendaja võib olla mängijate taga ja anda juhiseid
* Lubatud kommunikatsioonikanalid Discord, Teamspeak, Mumble jms

### 4.6 Kaardid

Järgmised võimalikud kaardid:
* de_inferno
* de_nuke
* de_mirage
* de_train
* de_cache
* de_cbble
* de_overpass

### 4.7 Server

Kõik serverid toimuvad 128 tickistes serverites.
Kõik serverid on lokaalses võrgus (LAN).

## 5 Võistluse struktuur

* Grupi tasand - 4 tiimi pääsevad edasi
* Double elimination - Ülemine võistlus parim ühest, veerandfinaal parim kolmest, semi finaal parim kolmest,
 finaal parim viiest, alumine võistlus parim ühest
* Täpne võistluse struktuur ja tiimide väljapanek selgub kohapeal

## Erilised tänud

* **ESL Gaming Network** - *baas reeglid* 
