##########################################################
## Author:   Josh Morris
## Filename: wordle_lib.py
## Purpose:  Library of utility functions for the
##           CS321 final project Wordle game.
##########################################################

import random
from datetime import date

WORD_LENGTH = 5
MAX_GUESSES = 6

CORRECT = 2  ## right letter, right position
PRESENT = 1  ## right letter, wrong position
ABSENT  = 0  ## letter not in word

WORDS = [
    "CRIMP", "GROAN", "FLASK", "TWIST", "PLUMB",
    "DWARF", "FLUNG", "CRASH", "JOUST", "KNELT",
    "SCALP", "TROVE", "BLUNT", "CREPT", "SWAMP",
    "SWORD", "GLINT", "PERCH", "STOMP", "WORDY",
]


def get_word_of_the_day():
    ## Seed with today's date so same day always returns same word
    today = date.today()
    seed  = today.year * 10000 + today.month * 100 + today.day
    return random.Random(seed).choice(WORDS)


def evaluate_guess(target, guess):
    ## Two-pass approach to handle duplicate letters correctly
    results       = [ABSENT] * WORD_LENGTH
    target_used   = [False]  * WORD_LENGTH
    guess_matched = [False]  * WORD_LENGTH

    for i in range(WORD_LENGTH):
        if guess[i] == target[i]:
            results[i]       = CORRECT
            target_used[i]   = True
            guess_matched[i] = True

    for i in range(WORD_LENGTH):
        if guess_matched[i]:
            continue
        for j in range(WORD_LENGTH):
            if not target_used[j] and guess[i] == target[j]:
                results[i]     = PRESENT
                target_used[j] = True
                break

    return results


def is_valid_word(word):
    return len(word) == WORD_LENGTH and word.isalpha()


def format_result_string(guess, results):
    
    return "RESULT:{}:{}".format(guess, "".join(str(r) for r in results))


def check_win(results):
    return all(r == CORRECT for r in results)


def to_upper(word):
    return word.upper()
