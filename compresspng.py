import os, subprocess

def march(directory):
    for current_dir, sub_dirs, files in os.walk(directory):
        for name in files:
            if name.lower().endswith(".png"):
                path = os.path.join(current_dir, name)
                subprocess.run(["pngout", path, "/y"])
        for directory in sub_dirs:
            march(directory)

if __name__ == "__main__":
    resourcesPath = os.path.join(os.getcwd(), "src", "main", "resources")
    march(resourcesPath)
