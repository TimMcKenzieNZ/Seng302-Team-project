import sys
import random
import math

USERNAME_START = "TAG"

numArgs = len(sys.argv)
args = sys.argv

if numArgs != 2:
    print("Format " + args[0] + " <num_donors>")
    sys.exit()

num_donors = int(args[1])
f = open("script.txt","w+")

boyNames = ["Noah", "Liam", "William", "Benjamen", "Jacob", "Ethan", "Alexander", "Harper", "Logan", "Mason", "Elijah", "Oliver", "Alexander", "Daniel", "Samuel", "Jayden", "Wyatt", "Carter", "Wyatt", "Walter", "Lincoln", "Levi", "Julian", "Oscar", "Harry","Tony", "Timmy"]
girlNames = ["Emma", "Olivia", "Ava", "Issabella", "Sophia", "Mia", "Charlotte", "Amelia", "Evelyn", "Aigail", "Harper", "Emily", "Elizabeth", "Sofia", "Chloe", "Selina", "Riley", "Layla", "Madison", "Lily", "Natalie", "Eleanor", "Marge", "Lisa", "Margary"]
unisexNames = ["Alex", "Ayden", "Bailey", "Kendall", "Billy", "Randy", "Willie", "Aubery", "Carson", "Bronwyn", "Indigo", "Jesse", "Jody", "Kei", "Kyle", "Piper", "Quinn", "Reese", "Roose", "Mickey", "Sloan", "Storm", "Wesley"]

boyMiddleNames = ["James", "John", "William", "Thomas", "Alexander", "Robert", "Michael", "David", "Andrew", "Peter" "Joseph", "Jack ","Edward","Anthony","Christopher","Daniel","Henry"]
girlMiddleNames = ["Rose","Grace","Jane","Louise","Jade","May","Elizabeth","Marie","Kate","Anne","Lily","Jean","Ruby","Paige","Lee","Belle","Claire","Mary","Charlotte","Hope"]
unisexMiddleNames = ["Harper", "Maredith", "Adair", "Rowan", "Hayden", "Dale", "Blair", "Sutton", "Robin", "Merrit", "Briar", "Sawyer", "Linden","Lane"]

lastNames = ["Potter","Smith","Johnson","Williams","Brown","Jones","Miller","Davis","Garcia","Rodriguez","Wilson","Hayes","Lewis","Collins","Stark","Butler", "Gonzales", "Simmons", "Gray", "Silver", "Parker", "Turner", "Mitchel"]

cities = ["Eastbourne" ,"Edendale" ,"Edgecumbe" ,"Egmont Village" ,"Eketahuna" ,"Eltham" ,"Ettrick" ,"Eyrewell Forest" ,"Fairhall" ,"Fairlie" ,"Featherston" ,"Feilding" ,"Fernside" ,"Flaxmere" ,"Flaxton" ,"Fox Glacier" ,"Foxton" ,"Foxton Beach" ,"Frankton" ,"Otago" ,"Frankton" ,"Waikato" ,"Franz Josef" ,"Geraldine" ,"Gisborne" ,"Glenorchy" ,"Glentui" ,"Gore","Granity","Greymouth","Greytown","Grovetown","Gummies Bush","Haast","Hakataramea","Halcombe","Hamilton","Hampden","Hanmer Springs","Hari Hari","Hastings","Haumoana","Haupiri","Havelock","Havelock North","Hawea","Hawera","Helensville","Henley","Herbert","Herekino","Hikuai","Hikurangi","Hikutaia","Hinuera","Hokitika","Hope","Horeke","Houhora","Howick","Huapai","Huiakama","Huirangi","Hukerenui","Hunterville","Huntly","Hurleyville","Inangahua Junction","Inglewood","Invercargill","Jacks Point","Jacobs River","Kaeo","Kaiapoi","Kaihu","Kaikohe","Kaikoura","Kaimata","Kaingaroa","Kaipara Flats","Kairaki","Kaitaia","Kaitangata","Kaiwaka","Kakanui","Kakaramea","Kaniere","Kaponga","Karamea","Karetu","Karitane","Katikati","Kaukapakapa","Kauri","Kawakawa","Kawerau","Kennedy Bay","Kerikeri","Kihikihi","Kingston","Kinloch","Kirwee","Kohukohu","Koitiata","Kokatahi","Kokopu","Koromiko","Kumara","Kumeu","Kurow","Lauriston","Lawrence","Leeston","Leigh","Lepperton","Levin","Lincoln","Linkwater","Little River","Loburn","Lower Hutt","Luggate","Lumsden","Lyttelton","Makahu","Manaia","Coromandel","Manaia", "Taranaki","Manakau","Manapouri","Mangakino","Mangamuka","Mangatoki","Mangawhai","Manukau","Manurewa","Manutahi","Mapua","Maraetai","Marco","Maromaku","Marsden Bay","Martinborough","Marton","Maruia","Masterton","Matakana","Matakohe","Matamata","Matapu","Matarangi","Matarau","Matata","Mataura","Matihetihe","Maungakaramea","Maungatapere","Maungaturoto","Mayfield","Meremere","Methven","Middlemarch","Midhirst","Millers Flat","Milton","Mimi","Minginui","Moana","Moawhango","Moenui","Moeraki","Moerewa","Mokau","Mokoia","Morrinsville","Mosgiel","Mossburn","Motatau","Motueka","Mount Maunganui","Mount Somers","Murchison","Murupara","Napier","Naseby","Nelson","New Brighton","New Plymouth","Ngaere","Ngamatapouri","Ngapara","Ngaruawahia","Ngataki","Ngatea","Ngongotaha","Ngunguru","Nightcaps","Norfolk","Normanby","Norsewood","Oakura","Oamaru","Oban","Ohaeawai","Ohakune","Ohangai","Ohoka","Ohope Beach","Ohura","Okaihau","Okato","Okuku","Omanaia","Omarama","Omata","Omokoroa","Onewhero","Opononi","Opotiki","Opua","Opunake","Oratia","Orewa","Oromahoe","Oruaiti","Otaika","Otaki","Otakou","Otautau","Otiria","Otorohanga","Owaka","Oxford","Paekakariki","Paeroa","Pahiatua","Paihia","Pakaraka","Pakiri","Pakotai","Palmerston","Palmerston North","Pamapuria","Panguru","Papakura","Papamoa","Paparoa","Paparore","Papatoetoe","Parakai","Paraparaumu","Paremoremo","Pareora","Paroa","Parua Bay","Patea","Pauanui","Pauatahanui","Pegasus","Peka Peka","Pembroke","Peria","Petone","Picton","Piopio","Pipiwai","Pirongia","Pleasant Point","Plimmerton","Pokeno","Porirua","Poroti","Port Chalmers","Portland","Portobello","Pukekohe","Pukepoto","Pukerua Bay","Pukeuri","Punakaiki","Purua","Putaruru","Putorino","Queenstown","Raetihi","Raglan","Rahotu","Rai Valley","Rakaia","Ramarama","Ranfurly","Rangiora","Rapaura","Ratapiko","Raumati","Rawene","Rawhitiroa","Reefton","Renwick","Reporoa","Richmond","Riverhead","Riverlands","Riversdale","Riversdale Beach","Riverton","Riwaka","Rolleston","Ross","Rotorua","Roxburgh","Ruakaka","Ruatoria","Ruawai","Runanga","Russell","Saint Andrews","Saint Arnaud","Saint Bathans","Sanson","Seacliff","Seddon","Seddonville","Sefton","Shannon","Sheffield","Silverdale","Snells Beach","Spring Creek","Springfield","Springston","Stirling","Stratford","Swannanoa","Taharoa","Taieri Mouth","Taihape","Taipa-Mangonui","Tairua","Takaka","Tangiteroria","Tangowahine","Tapanui","Tapawera","Tapora","Tapu","Taradale","Tauhoa"]

regions = ["",""Chatham Islands","Auckland", "Auckland", "Auckland", "Canterbury", "Wellington", "Waikato","Auckland", "Auckland",
           "Auckland", "Canterbury", "Wellington", "Waikato","Bay of Plenty",
           "Manawatu-Wanganui","Otago","Northland", "Hawkes Bay","Auckland", "Auckland", "Auckland", "Canterbury", "Wellington", "Waikato","Auckland", "Auckland",
           "Auckland", "Canterbury", "Wellington", "Waikato","Bay of Plenty",
           "Manawatu-Wanganui","Otago","Northland", "Hawkes Bay","Taranaki","Southland","Nelson","Tasman", "Gisborne", "Marlborough","West Coast"]

suburbs = ["Clendon Park", "Clevedon", "Clover Park","Cockle Bay","Conifer Grove","Stillwater","Stonefields","Sunnyhills","Sunnynook",
"Sunnyvale", "Swanson", "Bishopdale","Bottle Lake","Bromley", "Brooklands", "Heathcote Valley", "Hei Hei", "Hillsborough", "Hoon Hay",
"Hornby","Otumoetai","Papamoa","Welcome Bay","Mangapapa","Whataupoko"]

genders = ['M', 'F', 'U', 'O']

bloodTypes = ["O-","O+","A+","A-","B+","B-","AB+","AB-"]

street_names = ["English", "Moorhouse","Victoria"]
street_types = ["Street", "Avenue", "Place", "Lane", "Road", "Terrace"]

chanceLivedInUk = 0.01
chanceSmoker = 0.2

chanceDead = 0.01

chanceLiverD = 0.2
chanceKidneysD = 0.2
chancePancreasD = 0.2
chanceHeartD = 0.2
chanceLungsD = 0.2
chanceIntestineD = 0.2
chanceCorneasD = 0.2
chanceMiddleEarsD = 0.2
chanceSkinD = 0.2
chanceBoneD = 0.2
chanceBoneMarrowD = 0.2
chanceConnectiveTissueD = 0.2

chanceLiverR = 0.1
chanceKidneysR = 0.1
chancePancreasR = 0.21
chanceHeartR = 0.1
chanceLungsR = 0.1
chanceIntestineR = 0.1
chanceCorneasR = 0.1
chanceMiddleEarsR = 0.1
chanceSkinR = 0.1
chanceBoneR = 0.1
chanceBoneMarrowR = 0.1
chanceConnectiveTissueR = 0.1

currentIndex = 0


def pad_zero(num, length):
    num = str(num)
    while len(num) < length:
        num = "0" + num
    return num

def random_donation_time(birthYear):
    rYear = math.floor(random.uniform(birthYear, 2018))
    rMonth = math.floor(random.uniform(1, 8))
    rDay= math.floor(random.uniform(1, 29))
    
    date = str(rYear) + "-" + pad_zero(rMonth,2) + "-" + pad_zero(rDay,2) + " 01:00"
    return date


for i in range(num_donors):
    username = USERNAME_START + pad_zero(i, 4)
    birthGender = random.choice(genders)
    gender = random.choice(genders)
    lastName = random.choice(lastNames)

    if gender == 'M':
        firstName = random.choice(boyNames)
        middleName = random.choice(boyMiddleNames)
    elif gender == 'F':
        firstName = random.choice(girlNames)
        middleName = random.choice(girlMiddleNames)
    else:
        firstName = random.choice(unisexNames)
        middleName = random.choice(unisexMiddleNames)

    livedInUkReal = random.uniform(0, 1)
    if livedInUkReal > chanceLivedInUk:
        livedInUkFlag = "0"
    else :
        livedInUkFlag = "1"

    randomYear = math.floor(random.uniform(1938, 2008))
    randomMonth = math.floor(random.uniform(1, 12))
    randomDay = math.floor(random.uniform(1, 29))

    dateOfBirth = str(randomYear) + "-" + pad_zero(randomMonth,2) + "-" + pad_zero(randomDay,2)
    
    creationYear = math.floor(random.uniform(randomYear,2017))
    creationMonth = math.floor(random.uniform(1,12))
    creationDay = math.floor(random.uniform(1,29))
    
    creationDate = str(creationYear) + "-" + pad_zero(creationMonth,2) + "-" + pad_zero(creationDay,2)

    height = str(math.floor(random.uniform(160, 240)))
    weight = str(math.floor(random.uniform(35,120)))

    bloodType = random.choice(bloodTypes)
    systolic = math.floor(random.uniform(120, 180))
    diastolic = math.floor(random.uniform(80, 120))
    bloodPressure = str(systolic) + "/" + str(diastolic)

    smokerReal = random.uniform(0, 1)
    if smokerReal > chanceSmoker:
        smoker = "0"
    else:
        smoker = "1"

    alcoholConsumption = str(math.floor(random.uniform(0, 10)))

    dliverReal = random.uniform(0,1)
    if dliverReal > chanceLiverD:
        dliver = "0"
    else:
        dliver = "1"

    dKidneysReal = random.uniform(0,1)
    if dKidneysReal > chanceKidneysD:
        dkidneys = "0"
    else:
        dkidneys = "1"

    dPancreasReal = random.uniform(0,1)
    if dPancreasReal > chancePancreasD:
        dpancreas = "0"
    else:
        dpancreas = "1"

    dHeartReal = random.uniform(0,1)
    if dHeartReal > chanceHeartD:
        dheart = "0"
    else:
        dheart= "1"

    dLungsReal = random.uniform(0,1)
    if dLungsReal > chanceLungsD:
        dlungs = "0"
    else:
        dlungs= "1"

    dIntestineReal = random.uniform(0,1)
    if dIntestineReal > chanceIntestineD:
        dintestine = "0"
    else:
        dintestine= "1"

    dCorneasReal = random.uniform(0,1)
    if dCorneasReal > chanceCorneasD:
        dcorneas = "0"
    else:
        dcorneas = "1"

    dMiddleEarsReal = random.uniform(0,1)
    if dMiddleEarsReal > chanceMiddleEarsD:
        dmiddleears = "0"
    else:
        dmiddleears = "1"

    dSkinReal = random.uniform(0,1)
    if dSkinReal  > chanceSkinD:
        dskin = "0"
    else:
        dskin = "1"

    dBoneReal = random.uniform(0,1)
    if dBoneReal > chanceBoneD:
        dbone = "0"
    else:
        dbone = "1"

    dBoneMarrowReal = random.uniform(0,1)
    if dBoneMarrowReal > chanceBoneMarrowD:
        dbonemarrow = "0"
    else:
        dbonemarrow = "1"

    dConnectiveTissueReal = random.uniform(0,1)
    if dConnectiveTissueReal > chanceConnectiveTissueD:
        dconnectivetissue = "0"
    else:
        dconnectivetissue = "1"

    rliverReal = random.uniform(0,1)
    if rliverReal > chanceLiverR:
        rliver = "0"
        rtimeliver = "1970-01-01 01:00"
    else:
        rliver = "1"
        rtimeliver = random_donation_time(randomYear)

    rKidneysReal = random.uniform(0,1)
    if rKidneysReal > chanceKidneysR:
        rkidneys = "0"
        rtimekidneys = "1970-01-01 01:00"
    else:
        rkidneys = "1"
        rtimekidneys = random_donation_time(randomYear)

    rPancreasReal = random.uniform(0,1)
    if rPancreasReal > chancePancreasR:
        rpancreas = "0"
        rtimepancreas = "1970-01-01 01:00"
    else:
        rpancreas = "1"
        rtimepancreas = random_donation_time(randomYear)

    rHeartReal = random.uniform(0,1)
    if rHeartReal > chanceHeartR:
        rheart = "0"
        rtimeheart = "1970-01-01 01:00"
    else:
        rheart= "1"
        rtimeheart = random_donation_time(randomYear)

    rLungsReal = random.uniform(0,1)
    if rLungsReal > chanceLungsR:
        rlungs = "0"
        rtimelungs = "1970-01-01 01:00"
    else:
        rlungs= "1"
        rtimelungs = random_donation_time(randomYear)

    rIntestineReal = random.uniform(0,1)
    if rIntestineReal > chanceIntestineR:
        rintestine = "0"
        rtimeintestine = "1970-01-01 01:00"
    else:
        rintestine= "1"
        rtimeintestine = random_donation_time(randomYear)

    rCorneasReal = random.uniform(0,1)
    if rCorneasReal > chanceCorneasR:
        rcorneas = "0"
        rtimecorneas = "1970-01-01 01:00"
    else:
        rcorneas = "1"
        rtimecorneas = random_donation_time(randomYear)

    rMiddleEarsReal = random.uniform(0,1)
    if rMiddleEarsReal > chanceMiddleEarsR:
        rmiddleears = "0"
        rtimemiddleears = "1970-01-01 01:00"
    else:
        rmiddleears = "1"
        rtimemiddleears = random_donation_time(randomYear)

    rSkinReal = random.uniform(0,1)
    if rSkinReal  > chanceSkinR:
        rskin = "0"
        rtimeskin = "1970-01-01 01:00"
    else:
        rskin = "1"
        rtimeskin = random_donation_time(randomYear)

    rBoneReal = random.uniform(0,1)
    if dBoneReal > chanceBoneR:
        rbone = "0"
        rtimebone = "1970-01-01 01:00"
    else:
        rbone = "1"
        rtimebone = random_donation_time(randomYear)
        

    rBoneMarrowReal = random.uniform(0,1)
    if rBoneMarrowReal > chanceBoneMarrowR:
        rbonemarrow = "0"
        rtimebonemarrow = "1970-01-01 01:00"
    else:
        rbonemarrow = "1"
        rtimebonemarrow = random_donation_time(randomYear)

    rConnectiveTissueReal = random.uniform(0,1)
    if rConnectiveTissueReal > chanceConnectiveTissueR:
        rconnectivetissue = "0"
        rtimeconnectivetissue = "1970-01-01 01:00"
    else:
        rconnectivetissue = "1"
        rtimeconnectivetissue = random_donation_time(randomYear)
        
    # Street address line 1 (random no + random street name + random street/place/avenue)  
    postcode = pad_zero(math.floor(random.uniform(0,9999)),4)
    Epostcode = pad_zero(math.floor(random.uniform(0,9999)),4)
    
    suburb = random.choice(suburbs)
    Esuburb = random.choice(suburbs)
    
    city = random.choice(cities)
    Ecity = random.choice(cities)
    
    region = random.choice(regions)
    Eregion = random.choice(regions)
    
    streetNo = math.floor(random.uniform(1,1000))
    streetName = random.choice(street_names)
    streetType = random.choice(street_types)
    streetaddressline1 = str(streetNo) + " " + str(streetName) + " " + str(streetType)
    
    EstreetNo = math.floor(random.uniform(1,1000))
    EstreetName = random.choice(street_names)
    EstreetType = random.choice(street_types)  
    Estreetaddressline1 = str(EstreetNo) + " " + str(EstreetName) + " " + str(EstreetType)
    
        
    UserSQL = "INSERT INTO Users (`username`, `active`, `creationDate`, `firstName`, `middleName`, `lastName`, `password`, `version`, `userType`) VALUES ('" + username + "', true, '" + creationDate + "', '" + firstName + "', '" + middleName + "', '" + lastName + "', 'password', '1', 'donor');"

    DonorReceiverSQL = "INSERT INTO DonorReceivers (`username`, `preferredName`, `livedInUKFlag`, `activeFlag`, `dateOfDeath`, `dateOfBirth`, `birthGender`, `title`, `gender`, `height`, `weight`, `bloodType`, `bloodPressure`, `smoker`, `alcoholConsumption`, `bodyMassIndexFlag`, `dLiver`, `dKidneys`, `dPancreas`, `dHeart`, `dLungs`, `dIntestine`, `dCorneas`, `dMiddleEars`, `dSkin`, `dBone`, `dBoneMarrow`, `dConnectiveTissue`, `rLiver`, `rKidneys`, `rPancreas`, `rHeart`, `rLungs`, `rIntestine`, `rCorneas`, `rMiddleEars`, `rSkin`, `rBone`, `rBoneMarrow`, `rConnectiveTissue`, `rTimeLiver`, `rTimeKidneys`, `rTimePancreas`, `rTimeHeart`, `rTimeLungs`, `rTimeIntestine`, `rTimeCorneas`, `rTimeMiddleEars`, `rTimeSkin`, `rTimeBone`, `rTimeBoneMarrow`, `rTimeConnectiveTissue`) VALUES ('" + username + "', '', " + livedInUkFlag + ", 1, NULL, '" + dateOfBirth + "', '" + birthGender + "', NULL, '" + gender + "', '" + height + "', '" + weight + "', '" + bloodType + "', '" + bloodPressure + "', '" + smoker + "', '" + alcoholConsumption + "', '0', " + dliver + ", " + dkidneys + ", " + dpancreas + ", " + dheart + ", " + dlungs + ", " + dintestine + ", " + dcorneas + ", " + dmiddleears + ", " + dskin + ", " + dbone + ", " + dbonemarrow + ", " + dconnectivetissue + ", " + rliver + ", " + rkidneys + ", " + rpancreas + ", " + rheart + ", " + rlungs + ", " + rintestine + ", " + rcorneas + ", " + rmiddleears + ", " + rskin + ", " + rbone + ", " + rbonemarrow + ", " + rconnectivetissue + ", '" + rtimeliver + "', '" + rtimekidneys + "', '" + rtimepancreas + "', '" + rtimeheart + "', '" + rtimelungs + "', '" + rtimeintestine + "', '" + rtimecorneas + "', '" + rtimemiddleears + "', '" + rtimeskin + "', '" + rtimebone + "', '" + rtimebonemarrow + "', '" + rtimeconnectivetissue + "');"

    # Normal and emergency
    contactDetailsSQL = "INSERT INTO ContactDetails (`mobileNumber`, `username`, `homeNumber`, `email`, `streetAddressLineOne`, `streetAddressLineTwo`, `suburb`, `city`, `region`, `postCode`, `countryCode`, `emergency`) VALUES ('', '" + username + "', '', '', '" + streetaddressline1 + "', '', '" + suburb + "', '" + city + "', '" + region + "', '" + postcode + "', 'NZ', 0),('', '" + username + "', '', '', '" + Estreetaddressline1 + "', '', '" + Esuburb + "', '" + Ecity + "', '" + Eregion + "', '" + Epostcode + "', 'NZ', 1);"

    f.write(UserSQL + "\n")
    f.write(DonorReceiverSQL + "\n")
    f.write(contactDetailsSQL + "\n")
    
f.close()
    
    