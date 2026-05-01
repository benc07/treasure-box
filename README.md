# TreasureBox

A collaborative savings application that helps families and friends save toward shared goals like travel, dining, or pets, with a social, interactive twist on tracking money.

## Overview

TreasureBox turns group saving into a fun and transparent experience. Users can create shared “Treasure Boxes” for specific goals, contribute funds, track expenses, and interact through a social transaction ledger.

## UI Design 
The TreasureBox app features a clean and intuitive user interface designed for seamless navigation.

### Main Screen
    •    Heading: At the center, the TreasureBox icon is displayed using a custom-designed font. The heading features our signature brown theme as the background color. On the right, a "+" button allows users to create a new box, navigating them to the AddBoxScreen.
    
    •    TreasureBox LazyColumn: Below the header is a primary LazyColumn that displays all TreasureBoxCards. Each card shows the box’s current balance, name, and participant avatars. The card's background is a user-uploaded image relevant to that specific box.
  
### Box Detail Screen
(Triggered by tapping a specific box card)
    •    Heading: The top section displays general box information. Below the text, a custom progress bar visualizes the gap between the current balance and the target goal.
    
    •    Buttons: Centered on the screen are two action buttons for Deposit and Withdraw, which direct users to their respective pages.
    
    •    Transaction LazyColumn: This section lists all historical transaction cards. Each card includes green/red icons to distinguish between deposits and withdrawals, the transaction name, username, timestamp, and amount.
    
    •    Interactive Comment Section: Our standout feature is the comment area integrated at the bottom of each transaction card. All users sharing the box can leave comments and interact under every expense, adding a social and playful element to daily financial tracking.
Withdraw & Deposit Pages

    •    The input amount is prominently displayed at the top, with a text field below for the transaction name. (The Withdraw page also displays the current box balance to ensure users do not exceed the available limit)
AddBox Screen

    •    This screen collects all necessary data to initialize a new TreasureBox, including: Box Name, Initial Balance, Target Goal, Participant IDs, and a Background Image.
