# FreezeUp

*App to keep track of what food is in your freezer*

## Description

FreezeUp lets you track what items of food are in your freezer. Each item stores useful information such as date entered, quantity remaining, type of food etc. The app allows sorting and browing of the freezer contents, and simple interactions to edit and and add to items recorded in the freezer.

## Use cases

### User puts a new item in the freezer

- Opens app
- Inventory screen displayed
- Adds new item (should be one click)
- User inputs:
    + Name of item
    + Type of item (from dropdown?)
    + Quantity (Number or category amount)
    + Location (?)
    + Date (defaults to today)
    + Image (via camera, gallery, or image search)
- Clicks save
- Return to inventory screen

### User removes an item from the freezer

- Opens app
- Inventory screen displayed
- Scroll/search for used item
- Decrements quantity by one unit/one category (from inventory screen)
- (If quantity goes to zero item is removed from inventory, and stored in previous items?)

### User searches for freezer items

- Opens app
- Inventory screen displayed
- Changes filter option to type/date added/alphabetical
- Searches within items (real time results/suggestions would be nice)
- Select item to bring up details

# Screens

## Inventory screen

Shows a list view of all items in the freezer. Contains a picture, title, date added, quantity, type, ability to decrement quantity. 
- Tap on item to edit.
- Tap on add button to add item
- Settings menu -> change sort options, about screen
- Search items

## Item screen

View details of item, edit fields, change picture, simple decrement button.
