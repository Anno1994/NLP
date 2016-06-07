class Coder:
    def __init__(self, dictionary):
        self.dictionary = dictionary

        self.mnemonics = {'2': 'ABC', '3': 'DEF', '4': 'GHI', '5': 'JKL',
                          '6': 'MNO', '7': 'PQRS', '8': 'TUV', '9': 'WXYZ'}

        self.charCode = dict((c, entry[0]) for entry in self.mnemonics.items() for c in entry[1])

        self.wordsForNum = {}
        for string in dictionary:
            self.wordsForNum.setdefault(self.__wordCode__(string), []).append(string)

    def __wordCode__(self, word):
        return "".join((self.charCode.get(c.upper()) for c in word))

    def encode(self, number):
        if not number:
            return tuple()
        else:
            s = set()
            for splitPoint in range(0, len(number)):
                word = tuple(self.wordsForNum.get(number[splitPoint:]))
                rest = self.encode(number[:splitPoint])
                s.add(word + rest)
            return s

    def translate(self, number):
        return " ".join(self.encode(number))


def main():
    coder = Coder(["java", "kata", "lava"])

    print(coder.encode("5282"))


if __name__ == "__main__":
    main()
