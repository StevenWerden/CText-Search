## Preface
This project was made in conjuction with Professor Paul Kjellberg and his research team at Whitter College in CA. 
This was initally supposed to be a [web-based / console-ran](https://replit.com/@StevenWerden/CText-Search) project since replit.com allowed that without a profile being made but their web UI changes made it unable to run as such. 

## Project Details
[ctext.org](ctext.org) The Chinese Text Project is the largest digital library of pre-modern Chinese texts in existence with a prexisting search function. The point of this project was to make a more specific search engine to find different types of Chinese characters and frequencies for the following categories: <br/>

- totalCharacters = The total amount of Chinese characters.
- currentCharacters = The total amount of a certain Chinese character.
- distinctCharacters = The amount of different types of Chinese characters.
- uniqueCharacters = The characters that only appear once.
- rareCharacters = The characters that appear 2-5 times.
- hapaxLegomenoi = Characters that only appear once across all literature.

## Limitations
Given the many webpages / subpages in each chapter, the code makes too many requests when trying to search through all the [Pre-Qin and Han](ctext.org/pre-qin-and-han), so the code stops searching after going through about 25-35% of the way through.
