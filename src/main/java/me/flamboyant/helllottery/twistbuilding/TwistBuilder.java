package me.flamboyant.helllottery.twistbuilding;

import me.flamboyant.utils.Common;
import me.flamboyant.causality.TriggerType;
import me.flamboyant.causality.TwistCausalityHandler;
import me.flamboyant.causality.causes.*;
import me.flamboyant.causality.consequences.*;
import me.flamboyant.causality.consequences.converter.LivingEntityToRainConsequenceConverter;
import me.flamboyant.causality.consequences.converter.TriggerToAllPlayersConsequenceConverter;
import me.flamboyant.causality.consequences.specific.ChestPopulateByLootAltitude;
import me.flamboyant.causality.consequences.specific.RegenChestLootConsequence;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TwistBuilder {
    private List<ATwistCause> causes = Arrays.asList(
            new BlockPlaceCause(),
            new CraftItemCause(),
            new EditBookCause(),
            new EntityDamagedCause(),
            new ExperienceGainCause(),
            new HitEntityCause(),
            new ItemConsumeCause(),
            new KillMobCause(),
            new MobHitPlayerCause(),
            new PlayerChatCause(),
            new PlayerFallDamageCause(),
            new PlayerInteractCause(),
            new TimerTriggerCause()
    );

    private List<ATwistConsequence> consequences = Arrays.asList(
            new DuplicateEntityConsequence(),
            new TeleportPlayerInAirsConsequence(),
            new ExplodeConsequence(),
            new PlacePrimedTntConsequence(),
            new SpawnHostileMobConsequence(),
            new IncreaseVelocityConsequence(),
            new PushLivingEntityConsequence(),
            new GiveRandomEffectConsequence(),
            new SpawnLootChestOnHealthConsequence(),
            new SpawnLootDependingOnBiomeConsequence(),
            new SpawnLootFromLootTableConsequence(),
            new ChestPopulateByLootAltitude(),
            new RegenChestLootConsequence(),
            new EnchantConsequence()
    );

    private int firstNeutralConsequenceIndex = 5;
    private int firstPositiveConsequenceIndex = 8;
    private List<Integer> rainableConsequences = Arrays.asList(3, 4, 8, 9, 10);
    private List<Integer> dispatchableConsequences = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13);

    //Probleme on peut pas différencier le positif ud négatif ici

    public TwistCausalityHandler buildRandomTwist(float difficulty, TwistType twistType, int twistPower, boolean isPositiveEffect) {
        if (isPositiveEffect) {
            buildRandomPositiveTwist(difficulty);
        }

        List<ATwistCause> selectedCauses = new ArrayList<>();
        List<ATwistConsequence> selectedConsequences = new ArrayList<>();

        int causeCpt = twistType == TwistType.MULTI_CAUSES || twistType == TwistType.MULTI_CROSSED ? twistPower : 1;
        int consequenceCpt = twistType == TwistType.MULTI_CONSEQUENCES || twistType == TwistType.MULTI_CROSSED ? twistPower : 1;

        ArrayList<ATwistCause> causesCopy = new ArrayList<>(causes);
        while (0 < causeCpt--)
        {
            ATwistCause cause = causesCopy.get(Common.rng.nextInt(causesCopy.size()));
            causesCopy.remove(cause);
            selectedCauses.add(cause);
            cause.resetParameters();
            cause.setSettings(difficulty, true);
        }

        List<TriggerType> commonTriggers = new ArrayList<>(selectedCauses.get(0).getTransmissibleTriggerTypes());
        for (int i = 1; i < selectedCauses.size(); i++) {
            for (int j = 0; j < commonTriggers.size(); j++) {
                if (!selectedCauses.get(i).getTransmissibleTriggerTypes().contains(commonTriggers.get(j))) {
                    commonTriggers.remove(commonTriggers.get(j--));
                }
            }
        }
        TriggerType selectedTrigger = commonTriggers.get(Common.rng.nextInt(commonTriggers.size()));
        boolean allPlayersMode = selectedTrigger == TriggerType.FLAT;
        selectedTrigger = allPlayersMode ? TriggerType.PLAYER : selectedTrigger;

        TriggerType finalSelectedTrigger = selectedTrigger;
        List<ATwistConsequence> filteredConsequences = consequences.subList(0, firstPositiveConsequenceIndex).stream().filter(c -> c.getAdmissibleTriggerTypes().contains(finalSelectedTrigger)).collect(Collectors.toList());
        if (filteredConsequences.size() < consequenceCpt)
            consequenceCpt = filteredConsequences.size();
        while (0 < consequenceCpt--) {
            ATwistConsequence csq = filteredConsequences.get(Common.rng.nextInt(filteredConsequences.size()));
            csq.resetParameters();
            csq.setSettings(difficulty, true);
            if (rainableConsequences.contains(consequences.indexOf(csq))
                    && Arrays.asList(TriggerType.ENTITY_ACTOR, TriggerType.ENTITY_TARGET, TriggerType.PLAYER).contains(selectedTrigger)
                    && Common.rng.nextFloat() < 0.2f)
                csq = new LivingEntityToRainConsequenceConverter(csq);
            if (allPlayersMode)
                csq = new TriggerToAllPlayersConsequenceConverter(csq);
            filteredConsequences.remove(csq);
            selectedConsequences.add(csq);
        }

        Bukkit.getLogger().info("Causes are : ");
        for (ATwistCause tata : selectedCauses) {
            Bukkit.getLogger().info(" - " + tata.getClass().getName());
        }
        Bukkit.getLogger().info("Consequences are : ");
        for (ATwistConsequence tata : selectedConsequences) {
            ATwistConsequence toPrint = tata;
            if (tata instanceof TriggerToAllPlayersConsequenceConverter) {
                TriggerToAllPlayersConsequenceConverter daddy = (TriggerToAllPlayersConsequenceConverter) tata;
                Bukkit.getLogger().info(" [under TriggerToAllPlayersConsequenceConverter]");
                tata = daddy.getChild();
            }
            if (tata instanceof LivingEntityToRainConsequenceConverter) {
                LivingEntityToRainConsequenceConverter daddy = (LivingEntityToRainConsequenceConverter) tata;
                Bukkit.getLogger().info(" [under LivingEntityToRainConsequenceConverter]");
                tata = daddy.getChild();
            }
            Bukkit.getLogger().info(" - " + tata.getClass().getName());
        }
        Bukkit.getLogger().info("Trigger is : " + selectedTrigger);

        return new TwistCausalityHandler(selectedCauses, selectedConsequences, Arrays.asList(selectedTrigger));
    }


    private TwistCausalityHandler buildRandomPositiveTwist(float difficulty) {
        ATwistCause cause = causes.get(Common.rng.nextInt(causes.size()));
        cause.resetParameters();
        cause.setSettings(difficulty, true);

        List<TriggerType> commonTriggers = cause.getTransmissibleTriggerTypes();
        TriggerType selectedTrigger = commonTriggers.get(Common.rng.nextInt(commonTriggers.size()));
        boolean allPlayersMode = selectedTrigger == TriggerType.FLAT;
        selectedTrigger = allPlayersMode ? TriggerType.PLAYER : selectedTrigger;

        TriggerType finalSelectedTrigger = selectedTrigger;
        List<ATwistConsequence> filteredConsequences = consequences.stream().filter(c -> c.getAdmissibleTriggerTypes().contains(finalSelectedTrigger)).collect(Collectors.toList());

        ATwistConsequence csq = filteredConsequences.get(Common.rng.nextInt(filteredConsequences.size()));
        csq.resetParameters();
        csq.setSettings(difficulty, true);
        if (rainableConsequences.contains(consequences.indexOf(csq))
                && Arrays.asList(TriggerType.ENTITY_ACTOR, TriggerType.ENTITY_TARGET, TriggerType.PLAYER).contains(selectedTrigger)
                && Common.rng.nextFloat() < 0.2f)
            csq = new LivingEntityToRainConsequenceConverter(csq);
        if (allPlayersMode)
            csq = new TriggerToAllPlayersConsequenceConverter(csq);


        ATwistConsequence toPrint = csq;
        Bukkit.getLogger().info("POS Causes are : ");
        Bukkit.getLogger().info(" - " + cause.getClass().getName());
        Bukkit.getLogger().info("Consequences are : ");
        if (csq instanceof TriggerToAllPlayersConsequenceConverter) {
            TriggerToAllPlayersConsequenceConverter daddy = (TriggerToAllPlayersConsequenceConverter) csq;
            Bukkit.getLogger().info(" [under TriggerToAllPlayersConsequenceConverter]");
            csq = daddy.getChild();
        }
        if (csq instanceof LivingEntityToRainConsequenceConverter) {
            LivingEntityToRainConsequenceConverter daddy = (LivingEntityToRainConsequenceConverter) csq;
            Bukkit.getLogger().info(" [under LivingEntityToRainConsequenceConverter]");
            csq = daddy.getChild();
        }
        Bukkit.getLogger().info(" - " + toPrint.getClass().getName());
        Bukkit.getLogger().info("Trigger is : " + selectedTrigger);

        return new TwistCausalityHandler(Arrays.asList(cause), Arrays.asList(csq), Arrays.asList(selectedTrigger));
    }
}
