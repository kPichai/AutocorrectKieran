# Runtime
#### A word-suggestion project created by Zach Blick for Adventures in Algorithms at Menlo School in Atherton, CA
## Runtime Overview

Below is a simple runtime analysis of each of my major functions.

1) fillNGramArray(words) has a runtime of O(l*a) where l is the length of the input dictionary because it linearly loops 
through the entire dictionary. Within each word of the dictionary it calculates the of hashes of each ngram which can be
abstracted to a, the average length of each word in the dictionary.
2) generateHashes has a runtime of O(l) where l is the length of the string inputted. It generates one hash initially of
the ngram size then uses rabinkarp fingerprinting method (with a runtime of O(1)) to calculate each consecutive hash of the input
3) calculateEditDistance has a runtime of O(a*b) in which a is the length of the first string and b is the length of the second string
this is the runtime because in the method it loops through a 2d array (which has size a, b).
4) Lastly suggestCorrections method has a more complex runtime. First it gets the candidate lists which has a runtime of
O(l*a) where l is the length of mispelled word and a is the average number of words at each location in the nGrams array.
Then, the method calculates the edit distance of each candidate word compared to the misspelled word this leads to a runtime
of O(l*a + l*a(l*b)) where b is the average length of the candidate words. This simplifies to O(l*a(1 + l*b)) which is a roughly
O(l^2) run time. This isn't to bad as l typically won't be a very large number. 