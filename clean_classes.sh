#!/bin/bash
# -------------------------------------------------------------------
# clean_classes.sh
# Finds and deletes all .class files recursively from the current directory.
# Prompts for confirmation before deletion.
# -------------------------------------------------------------------

echo "Searching for .class files..."

# Find all .class files
class_files=$(find . -type f -name "*.class")

if [ -z "$class_files" ]; then
    echo "No .class files found."
    exit 0
fi

echo "The following .class files will be deleted:"
echo "--------------------------------------------"
echo "$class_files"
echo "--------------------------------------------"

# Ask for confirmation
read -p "Do you want to delete these files? (y/N): " confirm

if [[ "$confirm" =~ ^[Yy]$ ]]; then
    echo "Deleting files..."
    # Delete the files and show what is being deleted
    find . -type f -name "*.class" -print -delete
    echo "All .class files have been deleted."
else
    echo "Deletion cancelled."
fi