# BROWSER FINGERPRINT

## DEVELOPED BY

* Lê Thành Công - D3M team, Zalopay@VNG
* Trần Kiến Quốc - D3M team, Zalopay@VNG

## RESOURCES

* Folder **BF-Web**:
    * Is full resource and clear system architecture.
    * Is used for getting full and necessary information, and store useful document related to browser fingerprint.
* Folder **BF-GetInfo**:
    * Is compact resource.
    * Is used for getting some important information to analyze data.
* Folder **BF-ExportCSV**:
    * Is used for extracting all fields of information on MongoDB database to CSV file type.
    * Is also used for analyzing data to retrieve valuable conclusion or prediction.

## CURRENT PROBLEMS

1. **Risk of UUID**: Browser fingerprint (BF) now is created by `uuid`, so it is risky. When user has at least 2 Android devices, they can use BF on local storage of device B for BF of device A, and both of BFs have been validated appropriately and stored on database before. Therefore, in the future, BF must be not only created by `uuid` but also integrated with other important and unique information.
2. **Uniqueness**: To invent new algorithms, trainning models or solutions to make BF become `unique` is not a easy work, so this depends on collected information, how users use device/browsers app (personal information) and how you solve problems by techniques. In addition, here we use some attributes such as Canvas, WebGL, Audio, etc; those are good but still be changed if browser version is updated with long version distance (Ex: Browser version 74.xxx is up to 77.xxx). Last but not least, information is gotten through browser can also be fake or changed if users use special tools or their device are rooted for cheating purposes.
3. **More problems**: You can discover them gradually.

## REFERENCES

**Useful papers you can read:**
* *Browser-fingerprinting-hraska-diploma-thesis*, Hraska.
* *Pixel Perfect: Fingerprinting Canvas in HTML5*, Keaton Mowery and Hovav Shacham.
* *FP-STALKER: Tracking Browser Fingerprint Evolutions*, Antoine Vastel, Pierre Laperdrix, Walter Rudametkin, Romain Rouvoy.
* *(Cross-)Browser Fingerprinting via OS and Hardware Level Features*, Song Li, Yinzhi Cao, Erik Wijmans.
* *The Web Never Forgets: Persistent Tracking Mechanisms in the Wild*, Gunes Acar1, Christian Eubank2, Steven Englehardt2, Marc Juarez1
Arvind Narayanan2, Claudia Diaz.