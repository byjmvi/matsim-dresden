library(tidyverse)
library(ggplot2)
library(dplyr)
library(stringr)


# csv einlesen
persons <- read_delim(file.choose(), delim = ";") #persons.csv einlesen
trips   <- read_delim(file.choose(), delim = ";") %>%  filter(!str_detect(person, "commercialPersonTraffic|freight|goodsTraffic")) #trips.csv einlesen

# Daten anschauen
(anz_personen <- sum(persons$subpopulation == "person" ,na.rm = TRUE)) #Anzahl Personen
(anz_frauen <- sum(persons$subpopulation == "person" & persons$gender == "f", na.rm = TRUE)) #Anzahl Frauen
(anz_maenner <- sum(persons$subpopulation == "person" & persons$gender == "m", na.rm = TRUE)) #Anzahl Männern

persons %>% summarise(min_alter = min(age, na.rm = TRUE))
persons %>% summarise(max_alter = max(age, na.rm = TRUE))
persons %>% summarise(mittel_alter = mean(age, na.rm = TRUE))
persons %>% summarise(median_alter = median(age, na.rm = TRUE))

# Altersgruppen nach Dresdner Bevölkerungsprognose
alter_grenzen_lhdd <- c(0, 2, 5, 14, 17, 24, 29, 44, 59, 64, 74, 84, Inf)
alter_labels_lhdd <- c("0-2","3-5","6-14","15-17","18-24","25-29","30-44","45-59","60-64","65-74","75-84","85+")
persons %>% filter(!is.na(age)) %>% mutate(alter_gruppe_lhdd = cut(age, breaks = alter_grenzen_lhdd)) %>% count(alter_gruppe_lhdd, name = "anz_alter_lhdd") %>%  mutate(ant_alter_lhdd = anz_alter_lhdd / sum(anz_alter_lhdd, na.rm = TRUE) * 100)
df <- persons %>%
  mutate(alter_gruppe_lhdd = cut(age, breaks = alter_grenzen_lhdd, labels = alter_labels_lhdd, right = TRUE)) %>%
  count(alter_gruppe_lhdd, name = "anz_alter_lhdd") %>% 
  filter(!is.na(alter_gruppe_lhdd))
ggplot(df, aes(x = alter_gruppe_lhdd, y = anz_alter_lhdd)) +
  geom_col() +
  ylim(0, 2000)
  labs(x = "Altersgruppe", y = "Anzahl") +
  theme(axis.text.x = element_text(angle = 45, hjust = 1))
  
 
  #Altershäufigkeiten
  
  age_dist <- persons %>%
    group_by(age) %>%
    summarise(Haeufigkeit = n())
  
  ggplot(age_dist, aes(x = age, y = Haeufigkeit)) +
    geom_col(fill = "#4C72B0", width = 0.8) +
    labs(
      title = "Szenario xy",
      x = "Alter",
      y = "Häufigkeit"
    ) +
    scale_x_continuous(breaks = seq(0, 100, by = 10)) +
    theme_minimal(base_size = 14) +
    coord_cartesian(ylim = c(0, 1600)) +
    theme(
      plot.title = element_text(hjust = 0.5, face = "bold")
    )
  
  str(age_dist$age)
  

  
# Altersgruppen nach sächsicher Bevölkerungsprognose
  alter_grenzen_sachsen <- c(0, 6, 15, 25, 40, 65, Inf)
  alter_labels_sachsen <- c("0-6","6-15","15-25","25-40","40-65","65+")
  persons %>% mutate(alter_gruppe_sachsen = cut(age, breaks = alter_grenzen_sachsen)) %>% count(alter_gruppe_sachsen, name = "anz_alter_sachsen") %>%  mutate(ant_alter_sachsen = anz_alter_sachsen / sum(anz_alter_sachsen) * 100)
  df <- persons %>%
    mutate(alter_gruppe_sachsen = cut(age, breaks = alter_grenzen_sachsen, labels = alter_labels_sachsen, right = TRUE)) %>%
    count(alter_gruppe_sachsen, name = "anz_alter_sachsen") %>% 
    filter(!is.na(alter_gruppe_sachsen))
  ggplot(df, aes(x = alter_gruppe_sachsen, y = anz_alter_sachsen)) +
    geom_col() +
    ylim(0, 3000) +
  labs(x = "Altersgruppe", y = "Anzahl") +
    theme(axis.text.x = element_text(angle = 45, hjust = 1))

  #Heimatstandorte
  persons %>% count(homeRegioStaR17, sort = TRUE)  %>% mutate(Anteil = n / sum(n))
  
  #Wegeanzahl
  (anz_wege <- sum(!is.na(trips$person))) # Anzahl Gesamtwege
  (anz_mobil <- sum(length(unique(trips$person)))) # Anzahl mobiler Personen
  (ant_mobil <- anz_mobil/anz_personen *100) # Anteil mobiler Personen in %
  (mittlere_wege <- anz_wege/anz_mobil) #durchschnittliche Wegeanzahl der mobilen Personen
  (mittlere_wege <- anz_wege/anz_personen) # durchschnittliche Wegeanzahl aller Personen
  wegeanzahl_pers <- trips %>% 
    group_by(person) %>% 
    summarise(wege = max(trip_number, na.rm = TRUE))
  (mitt_wegeanzahl <- mean(wegeanzahl_pers$wege, na.rm = TRUE)) #durchschnittliche Wegeanzahl der mobilen Personen, anders berechnet
  
  
  #Modal Split
  (vmittel <- trips %>%
      count(main_mode, name = "vmittelwahl") %>%
      mutate(ant_vmittelwahl = vmittelwahl / sum(vmittelwahl)) %>%
      arrange(desc(vmittelwahl)))
  
  
  ggplot(vmittel, aes(x = "", y = vmittelwahl, fill = main_mode)) +
    geom_col(width = 1) +
    coord_polar(theta = "y") +
    geom_text(aes(label = paste0(round(ant_vmittelwahl * 100, 1), "%")),
              position = position_stack(vjust = 0.5)) +
    labs(
      title = "Verkehrsmittelwahl (Modal Split)",
      fill = "Verkehrsmittel"
    ) +
    theme_void()

  
  modal_split_wege <- trips %>%
    group_by(person, main_mode) %>%
    summarise(wegehaeufig_pers = n(), .groups = "drop") %>%
    complete(person, main_mode, fill = list(wegehaeufig_pers = 0))%>%
    group_by(main_mode) %>%
    summarise(
      wegehaeufig = mean(wegehaeufig_pers, na.rm = TRUE)
    ) %>% 
    mutate(
      modal_split_wege = wegehaeufig/sum(wegehaeufig) * 100
    )
  
  #spezifische Verkehrsleistung
  trips %>%
    filter(traveled_distance < 100000) %>% 
    group_by(person, main_mode) %>%
        summarise(
      weite_pers_modus = sum(traveled_distance, na.rm = TRUE),
      .groups = "drop"
    ) %>%
    group_by(main_mode) %>%
    summarise(
      spez_vleistung = mean(weite_pers_modus, na.rm = TRUE)
    ) %>% 
        mutate(
      modal_split_vleistung = spez_vleistung/sum(spez_vleistung) * 100
    )
  

  #Wegeweite
  trips %>% 
    filter(traveled_distance < 100000) %>% 
      summarise(
      mitt_weite = mean(traveled_distance / 1000, na.rm = TRUE)
    ) #durchschnittliche Wegeweite
  
  #Wegeweite, verkehrsmittelspezifisch
  trips %>%
    filter(traveled_distance < 100000) %>%
    group_by(main_mode) %>%
    summarise(
      mitt_weite_spezifisch = mean(traveled_distance / 1000, na.rm = TRUE),
      n = n(),
      .groups = "drop"
    )
  
  #Wegedauer
  trips %>%
    filter(traveled_distance < 100000) %>% 
    summarise(
      mitt_dauer_srv = mean(as.numeric(trav_time) / 60, na.rm = TRUE)
    ) #durchschnittliche Wegedauer
  
  trips %>%
    filter(!str_detect(person, "commercialPersonTraffic|freight|goodsTraffic")) %>%
    ggplot(aes(x = as.numeric(trav_time, units = "secs") / 60)) +
    geom_histogram(
      binwidth = 1,
      boundary = 0,
      aes(y = (..count..)/sum(..count..))
    ) +
    xlim(0, 120) +
    scale_y_continuous(labels = scales::percent_format()) +
    labs(
      title = "Reisezeit-Verteilung mobiler Personen (Minuten) in Prozent bis 120 min",
      x = "Reisezeit (Minuten)",
      y = "Anteil (%)"
    )
  
  
  # Wegedauer, verkehrsmittelspezifisch
  trips %>%
    filter(traveled_distance < 100000) %>%
    group_by(main_mode) %>%
    summarise(
      mitt_dauer_spezifisch = mean(as.numeric(trav_time) / 60, na.rm = TRUE),
      n = n(),
      .groups = "drop"
    )
  
  # Zeit im Verkehr
  zeit_pers <- trips %>%
    filter(traveled_distance < 100000) %>% 
    group_by(person) %>%
    summarise(
      gesamtzeit = sum(trav_time, na.rm = TRUE)
    )
  (mitt_zeit_pers <- as.numeric(mean(zeit_pers$gesamtzeit, na.rm = TRUE), units = "mins"))
  
  #Anzahl der Ausgänge
  (ausgänge_mit <- trips %>%
    group_by(person) %>%
    summarise(
      ausgang_pers = sum(str_detect(end_activity_type, "home"), na.rm = TRUE)
    ) %>%
    filter(ausgang_pers > 0) %>%   #keine 0 betrachten, nicht vor Mitternacht zurück zu Hause
    summarise(
      mittelwert = mean(ausgang_pers, na.rm = TRUE)
    ))
  
  

  
  