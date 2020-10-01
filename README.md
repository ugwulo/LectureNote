# LectureNote
*This app helps students in remote learning and collaboration*

## Instructions:
*You first need to have a github account to make any contributions, if you don't have one, you can sign up [here](https://github.com/).*

To make contributions:

### 1. Clone the repository

Open the terminal, go the folder where you want to clone, and type `git clone` followed by the URL of the repository, and add .git at the end of URL. For example, the git URL for this repository would be:
https://github.com/ugwulo/LectureNote.git
Your final command should look like this:
`git clone https://github.com/ugwulo/LectureNote.git`

Or

You can simply use the green button on the top right of the repository, and clone or download the code(and then unzip it at your desired location).

### 2. Create new branch

Once you have cloned the repository, go the the "LectureNote" folder, and create a new branch:
`git branch newbranch`
Name the branch descriptively, so the others contributing to project easily get an idea about what you're working on.

Now switch to new branch:
`git checkout newbranch`

Now make the changes to the code you wanted to make, and save the modified files.

### Add changes locally

After making changes, you will need to add them to the repository locally using command,
`git add .`
Or
`git add --all`

Now you need to commit the changes by using command:
`git commit`

*Note that it's important to describe what you changed in you commit so as to help other contributors understand the significance of your work, so make sure to give a precise description of the changes you made in the commit message.*

After you commit, you will be prompted with a text file to write what you changed. Or you can simply add you commit message in the git commit command itself like:
`git commit -m "Updated some code"`

### Make changes to the original repo on github
To push the changes to github, use command:
`git push`

After this, the changes will be reviewed by the authors/owners of repository and will be merged to original branch.

Congratulation! You have successfully contributed to open source😃


**Note:** *If you are interested to play around with the code or want to make some major changes, we recommend you to first fork this repository, test the changes first and then commit the actual changes to the main repository.*
