package com.liqihao.pojo.bean;

import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.pojo.dto.ArticleDto;
import org.springframework.beans.BeanUtils;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 背包管理类
 *
 * @author lqhao
 */
public class BackPackManager {
    private CopyOnWriteArrayList<Article> backpacks;
    private Integer size;
    private volatile Integer nowSize = 0;
    private volatile Integer articleId = 0;
    private List<Integer> needDeleteBagId = new ArrayList<>();

    public List<Integer> getNeedDeleteBagId() {
        return needDeleteBagId;
    }

    public void setNeedDeleteBagId(List<Integer> needDeleteBagId) {
        this.needDeleteBagId = needDeleteBagId;
    }


    public BackPackManager() {
    }

    public BackPackManager(Integer size) {
        backpacks = new CopyOnWriteArrayList<Article>();
        this.size = size;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getNewArticleId() {
        return ++articleId;
    }

    public CopyOnWriteArrayList<Article> getBackpacks() {
        return backpacks;
    }

    //背包格子是否足够
    public boolean canPutArticle(Article article) {
        //判断物品类型
        if (article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
            MedicineBean medicineBean = (MedicineBean) article;
            List<Article> medicines = backpacks.stream()
                    .filter(a -> a.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())).collect(Collectors.toList());
            //总数量
            Integer number = medicineBean.getQuantity();
            for (Article a : medicines) {
                MedicineBean temp = (MedicineBean) a;
                //物品类型
                if (medicineBean.getId().equals(temp.getId()) && number > 0) {
                    //判断是否已经满了
                    if (temp.getQuantity().equals(size)) {
                        continue;
                    }
                    Integer nowNum = temp.getQuantity();
                    Integer sum = nowNum + number;
                    //判断加上后是否已经超过99
                    if (sum <= size) {
                        //不超过加上
                        return true;
                    } else {
                        number = number - (size - temp.getQuantity());
                    }
                }
            }
            //表明背包中没有该物品或者该物品的数量都是99或者是剩余的 新建
            int gridNum = 0;
            if (number <= 0) {
                return true;
            }
            //生成新的格子
            while (number > 0) {
                if (number > ConstantValue.BAG_MAX_VALUE) {
                    number -= ConstantValue.BAG_MAX_VALUE;
                } else {
                    number = 0;
                }
                gridNum++;
            }
            if (nowSize + gridNum <= size) {
                return true;
            } else {
                return false;
            }
        } else if ((article.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode()))) {
            if (nowSize > size) {
                return false;
            }
            return true;
        }
        return false;
    }

    //整理背包
    public synchronized void clearBackPack(){
        CopyOnWriteArrayList<Article> newBackPack=new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Article> oldBackPack=getBackpacks();
        setBackpacks(newBackPack);
        articleId=0;
        for (Article a:oldBackPack) {
            if (a.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
                MedicineBean medicineBean = (MedicineBean) a;
                if (medicineBean.getBagId()!=null){
                    needDeleteBagId.add(medicineBean.getBagId());
                }
                medicineBean.setArticleId(getNewArticleId());
                medicineBean.setBagId(null);
                put(medicineBean);
            } else if ((a.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode()))) {
                //判断背包大小
                EquipmentBean equipmentBean = (EquipmentBean) a;
                if (equipmentBean.getBagId()!=null){
                    needDeleteBagId.add(equipmentBean.getBagId());
                }
                //设置背包物品id
                equipmentBean.setArticleId(getNewArticleId());
                equipmentBean.setBagId(null);
                put(equipmentBean);
            }
        }
    }
    //背包放入东西
    public boolean put(Article article) {
        //判断物品类型
        if (article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
            //查找背包中是否有
            MedicineBean medicineBean = (MedicineBean) article;
            List<Article> medicines = getBackpacks().stream()
                    .filter(a -> a.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())).collect(Collectors.toList());
            //总数量
            Integer number = medicineBean.getQuantity();
            for (Article a : medicines) {
                MedicineBean temp = (MedicineBean) a;
                //物品类型
                if (medicineBean.getId().equals(temp.getId()) && number > 0) {
                    //判断是否已经满了
                    if (temp.getQuantity().equals(ConstantValue.BAG_MAX_VALUE)) {
                        continue;
                    }
                    Integer nowNum = temp.getQuantity();
                    Integer sum = nowNum + number;
                    //判断加上后是否已经超过99
                    if (sum <= ConstantValue.BAG_MAX_VALUE) {
                        //不超过加上
                        temp.setQuantity(sum);
                        return true;
                    } else {
                        number = number - (ConstantValue.BAG_MAX_VALUE - temp.getQuantity());
                        temp.setQuantity(ConstantValue.BAG_MAX_VALUE);
                    }
                }
            }
            //表明背包中没有该物品或者该物品的数量都是99或者是剩余的 新建
            if (number != 0) {
                while (number > 0) {
                    MedicineBean newMedicine = new MedicineBean();
                    BeanUtils.copyProperties(medicineBean, newMedicine);
                    if (number > ConstantValue.BAG_MAX_VALUE) {
                        newMedicine.setQuantity(ConstantValue.BAG_MAX_VALUE);
                        number -= ConstantValue.BAG_MAX_VALUE;
                    } else {
                        newMedicine.setQuantity(number);
                        number = 0;
                    }
                    newMedicine.setArticleId(getNewArticleId());
                    getBackpacks().add(newMedicine);
                    nowSize++;
                }

                return true;
            }
        } else if ((article.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode()))) {
            //判断背包大小

            if ((size - nowSize) <= 0) {
                //背包一个格子的空间都没有 无法存放

                return false;
            } else {
                EquipmentBean equipmentBean = (EquipmentBean) article;
                //设置背包物品id
                equipmentBean.setArticleId(getNewArticleId());
                nowSize++;
                getBackpacks().add(equipmentBean);

                return true;
            }
        }
        return false;

    }

    //背包放入东西 按照数据库格式来存放
    public synchronized void putOnDatabase(Article article) {
        if (article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
            MedicineBean medicineBean = (MedicineBean) article;
            medicineBean.setArticleId(getNewArticleId());
            getBackpacks().add(medicineBean);
            nowSize++;
        } else if ((article.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode()))) {
            //判断背包大小
            EquipmentBean equipmentBean = (EquipmentBean) article;
            //设置背包物品id
            equipmentBean.setArticleId(getNewArticleId());
            nowSize++;
            getBackpacks().add(equipmentBean);
        }
    }

    //判断背包是否存在某样东西
    public synchronized boolean contains(Article a) {
        return getBackpacks().contains(a);
    }

    //减少某样物品数量/丢弃装备
    public synchronized Article useOrAbandonArticle(Integer articleId, Integer number) {
        for (Article a : getBackpacks()) {
            if (a.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
                MedicineBean medicineBean = (MedicineBean) a;
                if (medicineBean.getArticleId().equals(articleId)) {
                    if (number <= medicineBean.getQuantity()) {
                        //可以丢弃
                        medicineBean.setQuantity(medicineBean.getQuantity() - number);
                        //判断是否数量为0 为0则删除
                        if (medicineBean.getQuantity() == 0) {
                            //需要删除数据库的记录

                            getNeedDeleteBagId().add(medicineBean.getBagId());
                            ((MedicineBean) a).setBagId(null);
                            getBackpacks().remove(a);
                            nowSize--;

                        }
                        return medicineBean;
                    } else {
                        return null;
                    }
                }
            } else {
                EquipmentBean equipmentBean = (EquipmentBean) a;
                if (equipmentBean.getArticleId().equals(articleId)) {
                    //需要删除数据库的记录
                    getNeedDeleteBagId().add(equipmentBean.getBagId());
                    ((EquipmentBean) a).setBagId(null);
                    getBackpacks().remove(a);
                    nowSize--;
                    return a;
                }
            }
        }
        return null;
    }


    public Integer getNowSize() {
        return nowSize;
    }

    //获取背包依存放空间
    public void setNowSize(Integer nowSize) {
        this.nowSize = nowSize;
    }

    //获取背包的大小
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    //根据articleId获取物品信息
    public synchronized Article getArticleByArticleId(Integer articleId) {

        for (Article article : getBackpacks()) {
            Integer id = null;
            if (article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
                MedicineBean medicineBean = (MedicineBean) article;
                id = medicineBean.getArticleId();
            } else {
                EquipmentBean equipmentBean = (EquipmentBean) article;
                id = equipmentBean.getArticleId();
            }
            if (id.equals(articleId)) {
                return article;
            }
        }
        return null;
    }

    //获取背包内物品信息
    public synchronized ArrayList<ArticleDto> getBackpacksMessage() {
        ArrayList<ArticleDto> articleDtos = new ArrayList<>();
        for (Article article : getBackpacks()) {
            ArticleDto articleDto = new ArticleDto();
            if (article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())) {
                MedicineBean medicineBean = (MedicineBean) article;
                articleDto.setArticleId(medicineBean.getArticleId());
                articleDto.setId(medicineBean.getId());
                articleDto.setArticleType(medicineBean.getArticleType());
                articleDto.setQuantity(medicineBean.getQuantity());
                articleDto.setBagId(medicineBean.getBagId());
            } else {
                EquipmentBean equipmentBean = (EquipmentBean) article;
                articleDto.setArticleId(equipmentBean.getArticleId());
                articleDto.setId(equipmentBean.getId());
                articleDto.setArticleType(equipmentBean.getArticleType());
                articleDto.setQuantity(equipmentBean.getQuantity());
                articleDto.setBagId(equipmentBean.getBagId());
                articleDto.setNowDurability(equipmentBean.getNowDurability());
                articleDto.setEquipmentId(equipmentBean.getEquipmentId());
            }
            articleDtos.add(articleDto);
        }
        return articleDtos;
    }

    public void setBackpacks(CopyOnWriteArrayList<Article> backpacks) {
        this.backpacks = backpacks;
    }

    public static void main(String[] args) {
        Article a = new EquipmentBean();
        Article b = new MedicineBean();
        List<Article> articles = new ArrayList<>();
        articles.add(a);
        articles.add(b);
        for (Article c : articles) {
            if (c instanceof EquipmentBean) {
                EquipmentBean equipmentBean = (EquipmentBean) c;
                equipmentBean.setAttackAdd(10);
                System.out.println(equipmentBean.getAttackAdd());
            } else if (c instanceof MedicineBean) {
                MedicineBean medicineBean = (MedicineBean) (c);
                medicineBean.setDescription("蓝药");
                System.out.println(medicineBean.getDescription());
            }
        }
    }
}
