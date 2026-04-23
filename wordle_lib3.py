#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Apr 21 15:00:53 2026

@author: joshmorris
"""

##########################################################
## Author:   Josh Morris
## Filename: wordle_lib2.py
## Purpose:  Library of utility functions for the
##           CS321 final project Wordle game.
##########################################################
import random
import sys
from datetime import date

WORD_LENGTH = 5
MAX_GUESSES = 6

WORDS = [
    "CRIMP", "GROAN", "FLASK", "TWIST", "PLUMB",
    "DWARF", "FLUNG", "CRASH", "JOUST", "KNELT",
    "SCALP", "TROVE", "BLUNT", "CREPT", "SWAMP",
    "SWORD", "GLINT", "PERCH", "STOMP", "WORDY",
]

## list to track all player sessions
## each entry records: user_id, name, result, guesses_used, date_played
player_log = []


def get_word_of_the_day():
    ## date-based seed so the same day always returns the same word
    today = date.today()
    seed  = today.year * 10000 + today.month * 100 + today.day
    return random.Random(seed).choice(WORDS)


def is_valid_word(word):
    return len(word) == WORD_LENGTH and word.isalpha()


def to_upper(word):
    return word.upper()


def record_player_result(user_id, result, guesses_used):
    ## result should be "WIN" or "LOSE" to match the Java client
    entry = {
        "user_id":      user_id,
        "result":       result,
        "guesses_used": guesses_used,
        "date_played":  date.today().isoformat()
    }
    player_log.append(entry)
    return entry


def get_player_log():
    return player_log


def get_player_history(user_id):
    ## return all entries for a given user
    return [entry for entry in player_log if entry["user_id"] == user_id]

def check_guess(guess, word): ##moved from java
    result = []

    for i in range(5): ##check letter by letter like the other version from java
        if guess[i] == word[i]:
            result.append("G")
        elif guess[i] in word:
            result.append("Y")
        else:
            result.append("B")

    return "".join(result) ##returns the color codes

import sys ##logic for server communication

if __name__ == "__main__":
    cmd = sys.argv[1]

    if cmd == "WOTD":
        print(get_word_of_the_day())

    elif cmd == "GUESS":
        guess = sys.argv[2]
        word = sys.argv[3]
        print(check_guess(guess, word))

    elif cmd == "VALID":
        word = sys.argv[2]
        print(is_valid_word(word))