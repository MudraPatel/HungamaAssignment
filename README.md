# HungamaAssignment

* MVVM (KOTLIN) 
* ViewModel
* LiveData
* Navigation Component
* Room

* Add dummy url to load the video in exoplayer
* Search Function -
  1. First load the list using like operator then later iterate that list and split the title based on spaces
  2. Iterate (Title List) SplitList 
  3. Check if search String contains space or not - usings conatins operator
  4. If yes then add into the list and apply break function otherwise same item will repeated (depends on title split array size )
  5. Else: Title list (item) is startwith search key or not, using startwith function
  6. if it start with search key then add in list
  
  * Recent Search - 
  1. User search the movie and click on it then it store in room db
  2. If my recentlist size limit is greater than 5 then delete old data.
  
* Loading the image - used bitmap and async task 
* Network Call - Used HttpsConnection
  

