import os

print(os.listdir())

for file in os.listdir():
    length = len(file) -4
    if(file[length:] != ".png"):
        continue
    print(file[0:length-2] + file[length-1:])
    os.rename(file, file[0:length-2] + file[length-1:])
