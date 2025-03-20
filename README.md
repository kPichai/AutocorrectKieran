# Autocorrect
#### A word-suggestion project created by Zach Blick for Adventures in Algorithms at Menlo School in Atherton, CA
## High Level Overview
To run this code, simply run the main function. The autocorrector will prompt you to enter a word you might have misspelled.
Upon typing a word in, the autocorrector will return a list of words within an edit distance less than a threshold limit.
The code works by calculating ngrams of words in a dictionary and storing their hashes, then when you enter a word it generates
ngrams of your word. It compares your ngrams to the dictionary ngrams to generate a narrowed list of candidate words. Lastly,
it calculates the levenshtein distance between each candidate word and yours then returns all less than the threshold.

Good luck using this tool!
- Kieran