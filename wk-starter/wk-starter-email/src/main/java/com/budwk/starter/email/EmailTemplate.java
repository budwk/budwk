package com.budwk.starter.email;

/**
 * @author wizzer@qq.com
 */
public class EmailTemplate {

    public static String tplCode = "<table style=\"box-shadow: 0 0 5px rgba(0, 0, 0, .2);\" border=\"0\" width=\"800\" cellspacing=\"0\" cellpadding=\"0\"\n" +
            "       align=\"center\">\n" +
            "    <tbody>\n" +
            "    <tr>\n" +
            "        <td bgcolor=\"#ffffff\">\n" +
            "            <table border=\"0\" width=\"90%\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">\n" +
            "                <tbody>\n" +
            "                <tr>\n" +
            "                    <td height=\"30\">&nbsp;</td>\n" +
            "                </tr>\n" +
            "\n" +
            "                <tr>\n" +
            "                    <td height=\"40\">&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td style=\"font-size: 28px; font-family: Arial; font-weight: bold; color: #646464;\" align=\"center\">\n" +
            "                        ${title}\n" +
            "                    </td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td height=\"60\">&nbsp;</td>\n" +
            "                </tr>\n" +
            "                </tbody>\n" +
            "            </table>\n" +
            "        </td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "        <td bgcolor=\"#ffffff\">\n" +
            "            <table border=\"0\" width=\"90%\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">\n" +
            "                <tbody>\n" +
            "                <tr>\n" +
            "                    <td style=\"font-family: Arial; font-size: 16px; color: #646464; line-height: 24px;\"><br>您的验证码为：<b>${code}</b><br><br>请在\n" +
            "                        <b>${time}</b> 前使用此验证码完成验证。<br><br>谨上，<br>${author}\n" +
            "                    </td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td style=\"font-family: Arial; font-size: 16px; color: #646464; line-height: 24px;\">&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td align=\"center\" style=\"font-family: Arial; font-size: 16px; color: #646464; line-height: 24px;\">\n" +
            "                        这是自动电子邮件，请勿直接回复此电子邮件。\n" +
            "                    </td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td height=\"20\">&nbsp;</td>\n" +
            "                </tr>\n" +
            "                </tbody>\n" +
            "            </table>\n" +
            "        </td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "        <td style=\"font-family: Arial; font-size: 14px; font-weight: bold; color: #797979;\" align=\"center\"\n" +
            "            valign=\"middle\" height=\"80\"><a style=\"color: #797979; text-decoration: none;\" href=\"${http}\"\n" +
            "                                           target=\"_blank\" rel=\"noopener\">${domain}</a></td>\n" +
            "    </tr>\n" +
            "    </tbody>\n" +
            "</table>";

    public static String tplMsg = "<table style=\"box-shadow: 0 0 5px rgba(0, 0, 0, .2);\" border=\"0\" width=\"800\" cellspacing=\"0\" cellpadding=\"0\"\n" +
            "       align=\"center\">\n" +
            "    <tbody>\n" +
            "    <tr>\n" +
            "        <td bgcolor=\"#ffffff\">\n" +
            "            <table border=\"0\" width=\"90%\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">\n" +
            "                <tbody>\n" +
            "                <tr>\n" +
            "                    <td height=\"30\">&nbsp;</td>\n" +
            "                </tr>\n" +
            "\n" +
            "                <tr>\n" +
            "                    <td height=\"40\">&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td style=\"font-size: 28px; font-family: Arial; font-weight: bold; color: #646464;\" align=\"center\">\n" +
            "                        ${title}\n" +
            "                    </td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td height=\"60\">&nbsp;</td>\n" +
            "                </tr>\n" +
            "                </tbody>\n" +
            "            </table>\n" +
            "        </td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "        <td bgcolor=\"#ffffff\">\n" +
            "            <table border=\"0\" width=\"90%\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">\n" +
            "                <tbody>\n" +
            "                <tr>\n" +
            "                    <td style=\"font-family: Arial; font-size: 16px; color: #646464; line-height: 24px;\">" +
            "<br><b>${name}</b>，您好：" +
            "<br><br>${text}\n" +
            "                        \n" +
            "                        <br><br>谨上，<br>${author}\n" +
            "                    </td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td style=\"font-family: Arial; font-size: 16px; color: #646464; line-height: 24px;\">&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td align=\"center\" style=\"font-family: Arial; font-size: 16px; color: #646464; line-height: 24px;\">\n" +
            "                        这是自动电子邮件，请勿直接回复此电子邮件。\n" +
            "                    </td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td height=\"20\">&nbsp;</td>\n" +
            "                </tr>\n" +
            "                </tbody>\n" +
            "            </table>\n" +
            "        </td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "        <td style=\"font-family: Arial; font-size: 14px; font-weight: bold; color: #797979;\" align=\"center\"\n" +
            "            valign=\"middle\" height=\"80\"><a style=\"color: #797979; text-decoration: none;\" href=\"${http}\"\n" +
            "                                           target=\"_blank\" rel=\"noopener\">${domain}</a></td>\n" +
            "    </tr>\n" +
            "    </tbody>\n" +
            "</table>";

}
