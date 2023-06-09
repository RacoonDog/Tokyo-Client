import os, time

fileCount = 0
folderCount = 0
lineCount = 0
wordCount = 0
charCount = 0
meaningfulCharCount = 0
alphanumCharCount = 0

def march(directory):
    global fileCount, folderCount, lineCount, wordCount, charCount, meaningfulCharCount, alphanumCharCount

    for current_dir, sub_dirs, files in os.walk(directory):
        for name in files:
            if name.lower().endswith(".java"):
                fileCount += 1

                with open(os.path.join(current_dir, name), encoding="utf8") as file:
                    content = file.read().split("\n")
                    lineCount += len(content)
                    for line in content:
                        for word in line.split():
                            for char in word:
                                if char.isalnum():
                                    wordCount += 1
                                    break

                            for char in word.strip():
                                meaningfulCharCount += 1
                                if char.isalnum():
                                    alphanumCharCount += 1

                        charCount += len(line)

        for directory in sub_dirs:
            folderCount += 1
            march(directory)

if __name__ == "__main__":
    startTime = time.time_ns()

    march(os.getcwd())

    time = time.time_ns() - startTime

    print(f"Files: {fileCount}")
    print(f"Folders: {folderCount}")
    print(f"Line Count: {lineCount}")
    print(f"Word Count: {wordCount}")
    print(f"Char Count: {charCount}")
    print(f"Char NO SPACE Count: {meaningfulCharCount}")
    print(f"Char NON SPECIAL Count: {alphanumCharCount}")
    print(f"Took {time}ns or {time / 1000000}ms to run.")
