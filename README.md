# super-sparking-research
What I have gathered from SUPER SPARKING, yet another BT3 mod that takes after BT4.

# Introduction
SUPER SPARKING is a Sparking! METEOR mod put together by [Furuya](https://www.youtube.com/@Nothingoniu) and the modders he has especially thanked (and/or kindly borrowed from). Its biggest changes are the **map roster** (consisting of **150 maps**) and the character movesets (which can change from costume to costume, a feature recreated from [KkTeam's BT3 DLC ISO](https://www.youtube.com/watch?v=TmtFeX42B_0)).

Sparking! METEOR was used as a base because [dbz3_sc](https://github.com/HiroTex/dbz3_sc/blob/main/ghidra/slps_258.15.gzf), the current decompilation of Budokai Tenkaichi 3, is solely based on the Sparking! METEOR ELF (``slps_258.15``). Its contributor, [Vras](https://github.com/Vrass28), is also responsible for the code-related changes made to SUPER SPARKING. Transferring the changes over to the NTSC-U or PAL version of BT3 would take way longer, so NTSC-J was chosen out of convenience.

# AFS Information
The ISO comes with two new AFS files: ``PZS3JP3.AFS`` and ``PZS3JP4.AFS``.

With a total of 5 AFS files, each AFS is locked [in an identical fashion to AFS files from BT4](https://github.com/ViveTheModder/bt4-research).

To add another layer of irony, a locked ISO (BT4) had some of its assets included in this locked ISO. Talk about preventing theft... sorry, borrowing without approval.

Fortunately, it does have **some metadata (29136 bytes worth)** belonging to one of the AFS's, at position **9221941248** in the ISO. Typing ``Map_000_PS.pak`` in the search bar is the simplest way to get there.

# AFS Contents
The aforementioned 150 maps are stored at position **7353661440** of the ISO, and come down to **1822195504 bytes** (~1.7 GiB), not including the last 4 maps used for Character Reference and Shenron Mode.
For a full list of maps, check out the ``maps.csv`` file provided in the repository.

Since separating 300 map files (2 PAK files per map) is difficult, I decided to **automate the process**.

Same goes for the GSC files (located at position **123136000** in the ISO) and the PAK files accompanying them (for lip-syncing and subtitles, located at position **124180480** in the ISO). No metadata for them, unfortunately.
